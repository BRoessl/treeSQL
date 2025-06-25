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

A JSONPointer of this format will be called **r-JSONPointer** (or **rJP**) for "*ranged JSONPointer*". We also add a special range literal `..~` to traverse back the tree (like `cd ..`, which might help to build a relative path). Range literals can be reused as starting point of a *context-aware (relative) JSONPointer*. We can think of even more features (see below) as long as the path element ends with `~[a-z][a-z0-9_]*` or just `~`.

Any JSONPointer is a valid r-JSONPointer, but an r-JSONPointer is likely not a valid JSONPointer.

**Examples:**
- `/foofoo/~index` is a *ranged JSONPointer* (**rJP**) and `~index` is a range literal.
- `/foo/bar/..~` performs one back-traversal at `/foo/bar` and is virtually the same as `/foo`.
- `~myLiteral/bar` is a *context-aware (relative) rJP*, starting at whatever the literal "myLiteral" is supposed to be. E.g. "myLiteral" could be defined in a *root-based (absolute) rJP* "/here/is/~myLiteral".

## Ranged JSONPointer
In short, a ranged JSONPointer (r-JSONPointer or rJP) can
1. denote an arbitrary amount of JSONPointers at once which follow a certain path pattern (*"/fixed/~arbitrary/fixed_2/~arbitrary_2"*).
1. be named (*"/bar/\~name/foo"*) or unnamed (*"/bar/\~/foo"*).
1. be relative (*"~name/this_is_relative_to/tilde_name"*).
1. traverse back the tree hierarchy (*"/foo/bar/..~back_to_foo"*).
1. scan up or down within a predefined depth range (*"/{1,3}~going_deep_1_2_and_then_3_levels/foo_at_level_2_3_or_4"*).
1. go left or right by a predefined range of sibling nodes (*"/foo/5/[+1,+2]~sibling_at_index_6_or_7/content_of_sibling"*).
1. use regex pattern to specify what a range literal might match (*"/foo/(^a.\*)~starts_with_a"*).

# Examples
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
`SELECT first_level AS 'name', "first_level/age" AS 'age', "~first_level/hobbies/0" AS 'primary_hobby' FROM "/~first_level"`
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
