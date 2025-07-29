# treeSQL
An easy and accessible way to apply SQL queries on tree structures like JSON, XML or YAML. It uses nearly unmodified SQL syntax and produces tables as its primary result format.

## Introduction
treeSQL uses common SQL syntax, e.g. `SELECT ... FROM ... JOIN ... WHERE ... ORDER BY ... LIMIT ...` but uses a custom extension for [JSONPointer](https://datatracker.ietf.org/doc/html/rfc6901) which allows us to "point" to a range of values. To understand this, let's begin with a very simple JSON:

```json
{
    "foo": 42,
    "bar": true,
    "foobar": "barfoo",
    "foofoo": [5,6,7]
}
```
**Quick Recap:** JSONPointer uses `/` as path delimiter. JSONPointer **`/foo`** would result in `42`, JSONPointer **`/foofoo/0`** would result in `5` and so on. Due to the special meaning for `/` there are two special escape char sequences: `~0` is `~` and `~1` is `/`. JSONPointers can be quoted as JSON string value: `"/foofoo/0"`.

**Custom JSONPointer Extension:** Since `~` is a special escape char in JSONPointers we (mis)use it to mark a range of values, bound to a self-assigned name. E.g. `/foofoo/~index` is the representation of the value range `/foofoo/0`, `/foofoo/1` and `/foofoo/2`, where `~index` is some kind of a placeholder. We call `~index` a **range literal** and to avoid conflicts with escape sequences `~0` and `~1`, every range literal must match `~[a-z][a-z0-9_]*`.

A JSONPointer of this format will be called **r-JSONPointer** (or **rJP**) for "*ranged JSONPointer*". We also add a special range literal `~..` to traverse back the tree (like `cd ..`, which might help to build a relative path). Range literals can be reused as starting point of a *context-aware (relative) JSONPointer*. We can think of even more features (see below) as long as the path element starts with `~[a-z][a-z0-9_]*` or just `~`.

Any JSONPointer is a valid r-JSONPointer, but an r-JSONPointer is likely not a valid JSONPointer.

**Examples:**
- `/foofoo/~index` is a *ranged JSONPointer* (**rJP**) and `~index` is a range literal.
- `/foo/bar/~..` performs one back-traversal at `/foo/bar` and is virtually the same as `/foo`.
- `~my_literal/bar` is a *context-aware (relative) rJP*, starting at whatever the literal "my_literal" is supposed to be. E.g. "my_literal" could be defined in a *root-based (absolute) rJP* "/here/is/~my_literal".

## Ranged JSONPointer
In short, a ranged JSONPointer (r-JSONPointer or rJP) can
1. denote an arbitrary amount of JSONPointers at once which follow a certain path pattern (*"/fixed/~arbitrary/fixed_2/~arbitrary_2"*).
1. be named (*"/bar/\~name/foo"*) or unnamed (*"/bar/\~/foo"*).
1. be relative (*"~name/this_is_relative_to/tilde_name"*).
1. traverse back the tree hierarchy (*"/foo/bar/~back_to_foo.."*).
1. scan up or down within a predefined depth range (*"/~going_deep_1_2_and_then_3_levels{1,3}/foo_at_level_2_3_or_4"*).
1. go left or right by a predefined range of sibling nodes (*"/foo/5/~sibling_at_index_6_or_7[+1,+2]/content_of_sibling"*).
1. use regex pattern to specify what a range literal might match (*"/foo/~starts_with_a(^a.\*)"*).

## Examples
Let's get straight into it with some examples to learn how we combine **r-JSONPointers with SQL** statements:

### Example A - in its simplest form
#### Query
`SELECT index, ~index, @index FROM "/foofoo/~index"`
#### Applied On Data
```json
{
    "foo": 42,
    "bar": true,
    "foobar": "barfoo",
    "foofoo": [5,6,7]
}
```
#### Result
|index|~index|@index
|---|---|---|
|0|/foofoo/0|5|
|1|/foofoo/1|6|
|2|/foofoo/2|7| 

#### Conclusion

As you can see, we use an **rJP** in the FROM-clause, quoted as JSON string, and in the SELECT-clause we can reuse the **range literal** to access the *replacement, path or value* for each match. By convention the range literal *without preceding `~`* represents the *replacement*, and *with `~`* it represents the *full path* up to and including the literal itself. To *access the value*, we prefix the literal with `@`. Every match forms an entry in the result table.

*In SQL terms, you can think of an **rJP** as an SQL table, and the range literal's replacement as the SQL primary key.*

### Example B - with multiple range literals
#### Query
`SELECT array_name, array_index, @array_index FROM "/~array_name/~array_index"`
#### Applied On Data
```json
{
    "foo": 42,
    "bar": true,
    "foofoo": [5,6,7],
    "barbar": ["hello"]
}
```
#### Result
|array_name|array_index|@array_index
|---|---|---|
|foofoo|0|5|
|foofoo|1|6|
|foofoo|2|7| 
|barbar|0|hello| 

#### Conclusion

As you can see, we use an **rJP** with **multiple range literals**. Each ~literal is like a key and with multiple literals, you get something similar to an **SQL composite key**.

Only if you can replace all literals to a value-returning JSONPointer, it will get into the result table. In contrast to SQL, the values in a column do not have to be of the same type, e.g. *@array_index* contains numbers and strings.

### Example C - accessing and naming things 
#### Query
`SELECT first_level AS 'name', "~first_level/age" AS 'age', "~first_level/hobbies/0" AS 'primary_hobby' FROM "/~first_level"`
#### Applied On Data
```json
{
    "Peter": {"age": 30, "hobbies": ["Poker", "Racing"]},
    "Josh": {"age": 44}
}
```
#### Result
|name|age|primary_hobby|
|---|---|---|
|Peter|30|Poker|
|Josh|44|NULL|

#### Conclusion

We can use the '**AS**'-keyword (`first_level AS name`) to use friendlier names for the result table header. We can also access deeper into a structure by using the range literal as starting point for a *context-aware rJP*, so it is a JSONPointer not starting with `/` but with a range literal instead: `"first_level/age"`.

If such access does not return a value, there is still a table result entry, but it uses SQL's **NULL** to indicate missing values.

### Example D - conditions with WHERE
#### Query
`SELECT entry AS 'key', @entry AS 'value' FROM "/~entry" WHERE entry != @entry`
#### Applied On Data
```json
{
    "foo": "foo",
    "barfoo": 42,
    "bar": "bar"
}
```
#### Result
|key|value|
|---|---|
|barfoo|42|
#### Conclusion
We can access range literals in the WHERE clause as in the SELECT clause and use it to filter the result. In this example, we list only entries where the key is not equal to the value.

### Example E - combining ranged JSONPointers
Let's assume we have 3 people with left and right pockets in which they can put colored marbles. Who has marbles of the same color in both pockets?
#### Query
`SELECT name, @left_color, @right_color FROM "/~name/leftPocket/~left_color" JOIN "~name/rightPocket/~right_color" WHERE @left_color == @right_color`
#### Applied On Data
```json
{
    "Peter": {"leftPocket": ["green", "red"], "rightPocket": ["blue"]},
    "Anne": {"leftPocket": ["red"], "rightPocket": ["red", "blue"]},
    "Josh": {"leftPocket": [], "rightPocket": ["blue", "blue"]}
}
```
#### Result
|name|@left_color|@right_color|
|---|---|---|
|Anne|red|red|
#### Conclusion
We can use multiple ranged JSONPointers in the FROM clause and even reuse range literals if they have already appeared in another ranged JSONPointer, making them **context-aware**. This helps to shorthand some ranged JSONPointers. This is like a SQL table JOIN, where results get combined with a cross-matching key.

## Which data format is supported by treeSQL? - The Navigable Tree Providers Interface.

**treeSQL** does not only support JSON format. In fact, it can support any data format where nodes and values are "pointable" using JSONPointer syntax since it is the core of treeSQL. Thus, we can implement different providers to support a specific data format.

- **JSON** is the default data format and part of the treesql-core module. JSON leaf nodes (or value nodes) differentiate between numbers, boolean, strings and null.
- **YAML** is a data format quite similar to JSON, thus it is easy to adapt 1:1.
- **CSV** is a tabular data format and is not what you consider to be tree-like. But we can still point to *rows* using an index and point to *columns* using the column name. If we add some conventions, it can be easily used in treeSQL:
  - the CSV content has named columns, using a header row
  - you *first access the row by index* and get a "column-name-to-cell"-map
  - you *then access the cell by column name*
  - Example: `"/0/name"` points to the name cell entry of the first row
  - CSV does not differentiate between value types, thus a cell value is always a string.
  - CSV content might use different delimiter (`,`,`;`,...) and quotes (`"`, `'`,...) which must somehow be interpreted by the provider implementation
- **XML** is also a tree-like data structure and go-to query languages like *XPointer*, *XPath* or *XQuery* exist. For treeSQL, we have to consider some details:
  - In contrast to XPointer/XPath, JSONPointer is using 0-based indexing.
  - An XML root element is named, but JSONPointer access the root using `""`
  - An XML element can have named attributes, unnamed text content and named child elements. Elements with the same name can appear multiple times.
  - For treeSQL, we borrow as much as possible from XPath but such that it is unambiguous for us to apply it as JSONPointer:
    - given `<root><elements id="123">text</elements></root>`
    - use `"/elements"` to get a list of elements all with name "elements" under the root element, which name "root" is **not** used inside the JSONPointer itself
    - `"/elements/0/text()"` to access the "text" value of the first "elements" element under root. You have to use 0 explicitly even when there is only one element for that name. This is essentially the *same as the XPath expression = /root/elements[0]/text()*
    - `"/elements/0/@id"` to access the attribute "id" with value "123"
- A **File System** with its folders and files can also be queried with treeSQL. Any folder can be used as root object for treeSQL. If a value of a file is accessed using e.g. `"/subfolder/myfile.json"`, treeSQL expects a UTF-8 encoded text file to be compliant with a treeSQLs data string type. 
- Any **Line-Based Text Content** - as it is known for configuration or log files - can be accessed by its line number. Raw text might also be accessed by a name or index if we apply a **Regular Expression** with grouping.
  - using Regex `(?<firstname>\\w+) (?<lastname>\\w+), age (?<age>\\d+)` on a line-based text content, where line 5 contains `John Doe, age 30`, JSONPointer `"/5/age"` would yield `30`.

### Mixed or nested data format - How treeSQL might read opaque content using a specific data format.

A powerful feature of treeSQL is that we can apply queries on mixed content, e.g. on CSV, Log or JSON files "embedded" in a directory tree. Whenever we are on a leaf node of provider implementation A, e.g. the file content for the file system provider, we might used the content as input for another provider implementation B, e.g. to create a JSON object. A leaf node of A then becomes the root node for B. To "link" leaf of A with root of B, we need some kind of instruction. This instruction becomes part of the JSONPointer itself and we call it a directive. A directive is a special named range literal followed by a question mark and the type+arguments of the directive.

Example: `"/myFolder/myFile.json/~my_json_root?JSON/key_in_json"` uses the JSON directive `?JSON`. The newly created JSON object gets nested into the rooted directory tree and is named "my_json_root". The absolute path for a matching result would be `/myFolder/myFile.json/my_json_root/key_in_json`.

Notice that we can *not* attach A directly to B (e.g. `/myFolder/myFile.json/key_in_json`), we must use an intermediate name for the JSON root to not become ambigous:
- `/myFolder/myFile.json` points to a file. The value is the content as UTF-8 encoded string.
- `/myFolder/myFile.json/my_json_root` points to the JSON root. The value might be anything.
- if we attach A and B directly, we can not tell if `/myFolder/myFile.json` is the string content or the content interpreted as JSON object.

A directive might use arguments to specify constraints or interpretation behaviour.
- `"/subFolder/csv_special_A.csv/~my_csv?CSV(delimiter=;&quote='&has=foo)/1/foo"` uses the CSV directive and tells to use `;` as delimiter and `'` as quote. It also adds the constraint that the header row should contain a named column `foo`

Sometimes the name of the range literal for the directive might be used as argument as well. E.g. for XML, the named literal should be named to match the XML root name. Thus only XML files with such root name are part of the potential result set. This should mitigate confusion with XML using named root object.

## Aggregates - COUNT/MAX/MIN/SUM
### An example
Apply query<br>
`SELECT name, SUM(amounts) AS sum FROM "/bills/~row" GROUP BY "~row/customer_name" AS name AGGREGATE "~row/amount" AS amounts`
<br>on data
```json
{
  "bills": [
    {
      "id": "B001",
      "customer_name": "Alice Johnson",
      "amount": 120.5
    },
    {
      "id": "B002",
      "customer_name": "Bob Smith",
      "amount": 99.99
    },
    {
      "id": "B003",
      "customer_name": "Alice Johnson",
      "amount": 230.0
    },
    {
      "id": "B004",
      "customer_name": "Bob Smith",
      "amount": 45.75
    },
    {
      "id": "B005",
      "customer_name": "Emily Davis",
      "amount": 310.2
    }
  ]
}
```
results into
|name|sum|
|---|---|
|Alice Johnson|350.5|
|Bob Smith|145.74|
|Emily Davis|310.2|

### Explanation
In traditional SQL, you choose a column which values you want to group together and for other columns, you have to derive a single value using an aggregate function (SUM, COUNT, MAX, ...). In treeSQL, any expression is virtually a column, so your **GROUP BY** is followed by an expression (which probably yields a string or integer). You must use the **AS** keyword and reference it in the select statement, since the expression itself is not a valid identification. The aggregate function can be applied on lists, and these lists are created in the **AGGREGATE** clause.

## (DRAFT) Using a different ouput format than tables

Since treeSQL is designed to be executed on JSON-like data structure, you might want to get the query result as a JSON-like output format.

Tables do not know about a schema other than rows, columns and cells, but a JSON can be structured whatever you like to structure it. So you must specify the output schema. Let us explore how that could look like reusing a query from the examples section:
<hr>

`SELECT first_level AS 'name', "~first_level/age" AS 'age', "~first_level/hobbies/0" AS 'primary_hobby' FROM "/~first_level"` 
<br>
|name|age|primary_hobby|
|---|---|---|
|Peter|30|Poker|
|Josh|44|NULL|
<hr>

the **SELECT** statement is a list of expressions which will define the tables keys (aliases in header row) and values (expression evaluated and inserted into the row/cell). For JSON output, a header does no exist and keys exist on various levels inside nested objects. So we have to rethink how we transform the query results into a JSON object using a **SELECT JSON Template**.
```json
SELECT
{
  "first_level": {
    "'age'": "~first_level/age",
    "'primary_hobby'": "\"~first_level/hobbies/0\""
  }
}
FROM "/~first_level"`
```
The new ouput (JSON format) would look like this:
```json
{
  "Peter": {
    "age": 30,
    "primary_hobby": "Poker"
  },
  "Josh": {
    "age": 44,
    "primary_hobby": null
  }
}
```
How does this work? Well, the FROM statement "streams" the result rows into a template engine which will create maps, arrays and values according to the template. The template is a valid JSON, but **all** keys and values are treeSQL expressions. These expressions gets evaluated "row by row" and depending on the result, a new entry might be added to the output JSON.

The algorithm works like this:
- for each template (root or nested) object
- first init constant key expressions IF the object has just been created (init object)
- then for each input row, evaluate the template key expression.
  - if the result is null, ignore
  - if the result is a string or number (converted to string)
    - and the template value is a JSON object, create or follow the key in the output JSON and continue with the template object inside the output JSON subobject.
    - and the template value is a string expression without aggregation function, evaluate the expression
      - create the key with that value (even if null)
      - if the key already exist, the existing value gets overwritten (if new value not null)
    - and the template value is a string expression with an aggregation function, evaluate the expression
      - if the value is null, ignore and do not create the key.
      - else create the key with that value
      - if the key already exist, the value gets added, the aggregation function gets applied at the end
    - and the template value is an array with one string expression
      - evaluate the inner expression and add it to the array in the output JSON.
  - if the result is a list, use each item in that list to create (or not create) a key using the rules above
  - whenever the template value is an array with two template objects (if-else), the key expression result is not intendet to become a key in the output JSON. The first expression is used when the key expression yields true or a non-null value, and the second expression is used when the key expression yields false or a null value. The key expression is only used as condition and thus not inserted into the output JSON. The chosen template object is evaluated further.

There are some additional pitfalls and rules to consider:
1. The expression syntax of treeSQL is used for keys and values, thus everything is a JSON string to hold that expression. While `"first_level"` might look like a constant key, it is in fact treeSQL's expression for a range literal replacement. On the other side, `"'age'"` is the expression which becomes the constant value `"age"`.
1. A Ranged JSONPointers becomes a JSON String embedded into a JSON String. This might look ugly since additional escape rules must be followed, e.g. for `\"`for `"`.
1. Data types are preserved but the values displayed might differ. E.g. treeSQL's `NULL` becomes standard JSON `null`.
1. In table format, each result is a new row. In JSON format, this results row might be aggregated in a nested objects, similar to SQL's *GROUP BY*.
1. Each template key expression will create multiple object entries where the key is the expression result. If the template value is a object template, only results matching the key expression will 
1. JSON keys are unique, but if you use constant keys (a simple string literal expression) mixed with contextual expression keys (a range literal), name clashes can occur. 
