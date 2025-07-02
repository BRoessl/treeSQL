// Generated from /workspaces/treeSQL/src/main/antlr4/io/broessl/treesql/grammar/TreeSQL.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TreeSQLParser}.
 */
public interface TreeSQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(TreeSQLParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(TreeSQLParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#funcArgs}.
	 * @param ctx the parse tree
	 */
	void enterFuncArgs(TreeSQLParser.FuncArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#funcArgs}.
	 * @param ctx the parse tree
	 */
	void exitFuncArgs(TreeSQLParser.FuncArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#literalValue}.
	 * @param ctx the parse tree
	 */
	void enterLiteralValue(TreeSQLParser.LiteralValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#literalValue}.
	 * @param ctx the parse tree
	 */
	void exitLiteralValue(TreeSQLParser.LiteralValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#selectStmt}.
	 * @param ctx the parse tree
	 */
	void enterSelectStmt(TreeSQLParser.SelectStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#selectStmt}.
	 * @param ctx the parse tree
	 */
	void exitSelectStmt(TreeSQLParser.SelectStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#selectCore}.
	 * @param ctx the parse tree
	 */
	void enterSelectCore(TreeSQLParser.SelectCoreContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#selectCore}.
	 * @param ctx the parse tree
	 */
	void exitSelectCore(TreeSQLParser.SelectCoreContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#whereExpr}.
	 * @param ctx the parse tree
	 */
	void enterWhereExpr(TreeSQLParser.WhereExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#whereExpr}.
	 * @param ctx the parse tree
	 */
	void exitWhereExpr(TreeSQLParser.WhereExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#jsonTextValue}.
	 * @param ctx the parse tree
	 */
	void enterJsonTextValue(TreeSQLParser.JsonTextValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#jsonTextValue}.
	 * @param ctx the parse tree
	 */
	void exitJsonTextValue(TreeSQLParser.JsonTextValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#resultColumn}.
	 * @param ctx the parse tree
	 */
	void enterResultColumn(TreeSQLParser.ResultColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#resultColumn}.
	 * @param ctx the parse tree
	 */
	void exitResultColumn(TreeSQLParser.ResultColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#orderByStmt}.
	 * @param ctx the parse tree
	 */
	void enterOrderByStmt(TreeSQLParser.OrderByStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#orderByStmt}.
	 * @param ctx the parse tree
	 */
	void exitOrderByStmt(TreeSQLParser.OrderByStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#limitStmt}.
	 * @param ctx the parse tree
	 */
	void enterLimitStmt(TreeSQLParser.LimitStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#limitStmt}.
	 * @param ctx the parse tree
	 */
	void exitLimitStmt(TreeSQLParser.LimitStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#orderingTerm}.
	 * @param ctx the parse tree
	 */
	void enterOrderingTerm(TreeSQLParser.OrderingTermContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#orderingTerm}.
	 * @param ctx the parse tree
	 */
	void exitOrderingTerm(TreeSQLParser.OrderingTermContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#ascOrDesc}.
	 * @param ctx the parse tree
	 */
	void enterAscOrDesc(TreeSQLParser.AscOrDescContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#ascOrDesc}.
	 * @param ctx the parse tree
	 */
	void exitAscOrDesc(TreeSQLParser.AscOrDescContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#columnAlias}.
	 * @param ctx the parse tree
	 */
	void enterColumnAlias(TreeSQLParser.ColumnAliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#columnAlias}.
	 * @param ctx the parse tree
	 */
	void exitColumnAlias(TreeSQLParser.ColumnAliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link TreeSQLParser#functionName}.
	 * @param ctx the parse tree
	 */
	void enterFunctionName(TreeSQLParser.FunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeSQLParser#functionName}.
	 * @param ctx the parse tree
	 */
	void exitFunctionName(TreeSQLParser.FunctionNameContext ctx);
}