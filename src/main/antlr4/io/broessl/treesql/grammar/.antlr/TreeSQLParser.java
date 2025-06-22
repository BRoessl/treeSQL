// Generated from /workspaces/treeSQL/src/main/antlr4/io/broessl/treesql/grammar/TreeSQL.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class TreeSQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, OPEN_PAR=3, CLOSE_PAR=4, COMMA=5, STAR=6, PLUS=7, MINUS=8, 
		DIV=9, MOD=10, LT=11, LT_EQ=12, GT=13, GT_EQ=14, EQ=15, NOT_EQ=16, AND=17, 
		AS=18, ASC=19, DESC=20, FROM=21, IN=22, NOT_IN=23, LIMIT=24, MATCH=25, 
		NOT_MATCH=26, NOT=27, NULL=28, OR=29, ORDER_BY=30, SELECT=31, WHERE=32, 
		TRUE=33, FALSE=34, FUNC_NAME=35, NUMERIC_LITERAL=36, STRING_LITERAL=37, 
		SINGLE_LINE_COMMENT=38, JSON_TEXT_VALUE=39, RANGED_LITERAL=40, VALUE_AT_LITERAL=41, 
		PATH_VARIABLE=42;
	public static final int
		RULE_expr = 0, RULE_funcArgs = 1, RULE_literalValue = 2, RULE_selectStmt = 3, 
		RULE_selectCore = 4, RULE_whereExpr = 5, RULE_jsonTextValue = 6, RULE_resultColumn = 7, 
		RULE_orderByStmt = 8, RULE_limitStmt = 9, RULE_orderingTerm = 10, RULE_ascOrDesc = 11, 
		RULE_columnAlias = 12, RULE_functionName = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"expr", "funcArgs", "literalValue", "selectStmt", "selectCore", "whereExpr", 
			"jsonTextValue", "resultColumn", "orderByStmt", "limitStmt", "orderingTerm", 
			"ascOrDesc", "columnAlias", "functionName"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "' '", "' JOIN '", "'('", "')'", "','", "'*'", "'+'", "'-'", "'/'", 
			"'%'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'AND'", "'AS'", 
			"'ASC'", "'DESC'", "'FROM'", "'IN'", "'NOT IN'", "'LIMIT'", "'MATCH'", 
			"'NOT MATCH'", "'NOT'", "'NULL'", "'OR'", "'ORDER BY'", "'SELECT'", "'WHERE'", 
			"'TRUE'", "'FALSE'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "OPEN_PAR", "CLOSE_PAR", "COMMA", "STAR", "PLUS", "MINUS", 
			"DIV", "MOD", "LT", "LT_EQ", "GT", "GT_EQ", "EQ", "NOT_EQ", "AND", "AS", 
			"ASC", "DESC", "FROM", "IN", "NOT_IN", "LIMIT", "MATCH", "NOT_MATCH", 
			"NOT", "NULL", "OR", "ORDER_BY", "SELECT", "WHERE", "TRUE", "FALSE", 
			"FUNC_NAME", "NUMERIC_LITERAL", "STRING_LITERAL", "SINGLE_LINE_COMMENT", 
			"JSON_TEXT_VALUE", "RANGED_LITERAL", "VALUE_AT_LITERAL", "PATH_VARIABLE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "TreeSQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TreeSQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public LiteralValueContext literalValue() {
			return getRuleContext(LiteralValueContext.class,0);
		}
		public TerminalNode NOT() { return getToken(TreeSQLParser.NOT, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public FunctionNameContext functionName() {
			return getRuleContext(FunctionNameContext.class,0);
		}
		public TerminalNode OPEN_PAR() { return getToken(TreeSQLParser.OPEN_PAR, 0); }
		public FuncArgsContext funcArgs() {
			return getRuleContext(FuncArgsContext.class,0);
		}
		public TerminalNode CLOSE_PAR() { return getToken(TreeSQLParser.CLOSE_PAR, 0); }
		public TerminalNode STAR() { return getToken(TreeSQLParser.STAR, 0); }
		public TerminalNode DIV() { return getToken(TreeSQLParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(TreeSQLParser.MOD, 0); }
		public TerminalNode PLUS() { return getToken(TreeSQLParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(TreeSQLParser.MINUS, 0); }
		public TerminalNode LT() { return getToken(TreeSQLParser.LT, 0); }
		public TerminalNode LT_EQ() { return getToken(TreeSQLParser.LT_EQ, 0); }
		public TerminalNode GT() { return getToken(TreeSQLParser.GT, 0); }
		public TerminalNode GT_EQ() { return getToken(TreeSQLParser.GT_EQ, 0); }
		public TerminalNode NOT_EQ() { return getToken(TreeSQLParser.NOT_EQ, 0); }
		public TerminalNode EQ() { return getToken(TreeSQLParser.EQ, 0); }
		public TerminalNode IN() { return getToken(TreeSQLParser.IN, 0); }
		public TerminalNode NOT_IN() { return getToken(TreeSQLParser.NOT_IN, 0); }
		public TerminalNode MATCH() { return getToken(TreeSQLParser.MATCH, 0); }
		public TerminalNode NOT_MATCH() { return getToken(TreeSQLParser.NOT_MATCH, 0); }
		public TerminalNode AND() { return getToken(TreeSQLParser.AND, 0); }
		public TerminalNode OR() { return getToken(TreeSQLParser.OR, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(29);
				literalValue();
				}
				break;
			case 2:
				{
				setState(30);
				match(NOT);
				setState(31);
				match(T__0);
				setState(32);
				expr(10);
				}
				break;
			case 3:
				{
				setState(33);
				functionName();
				setState(34);
				match(OPEN_PAR);
				setState(35);
				funcArgs();
				setState(36);
				match(CLOSE_PAR);
				}
				break;
			case 4:
				{
				setState(38);
				functionName();
				setState(39);
				match(OPEN_PAR);
				setState(40);
				match(CLOSE_PAR);
				}
				break;
			case 5:
				{
				setState(42);
				match(OPEN_PAR);
				setState(43);
				expr(0);
				setState(44);
				match(CLOSE_PAR);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(104);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(102);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
					case 1:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(48);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(50);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(49);
							match(T__0);
							}
						}

						setState(52);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1600L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(54);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(53);
							match(T__0);
							}
						}

						setState(56);
						expr(10);
						}
						break;
					case 2:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(57);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(59);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(58);
							match(T__0);
							}
						}

						setState(61);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(63);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(62);
							match(T__0);
							}
						}

						setState(65);
						expr(9);
						}
						break;
					case 3:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(66);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(68);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(67);
							match(T__0);
							}
						}

						setState(70);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 129024L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(72);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(71);
							match(T__0);
							}
						}

						setState(74);
						expr(8);
						}
						break;
					case 4:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(75);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(77);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(76);
							match(T__0);
							}
						}

						setState(79);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 247463936L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(81);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(80);
							match(T__0);
							}
						}

						setState(83);
						expr(7);
						}
						break;
					case 5:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(84);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(86);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(85);
							match(T__0);
							}
						}

						setState(88);
						match(AND);
						setState(90);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(89);
							match(T__0);
							}
						}

						setState(92);
						expr(6);
						}
						break;
					case 6:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(93);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(95);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(94);
							match(T__0);
							}
						}

						setState(97);
						match(OR);
						setState(99);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==T__0) {
							{
							setState(98);
							match(T__0);
							}
						}

						setState(101);
						expr(5);
						}
						break;
					}
					} 
				}
				setState(106);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FuncArgsContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(TreeSQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(TreeSQLParser.COMMA, i);
		}
		public FuncArgsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcArgs; }
	}

	public final FuncArgsContext funcArgs() throws RecognitionException {
		FuncArgsContext _localctx = new FuncArgsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_funcArgs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8513027833864L) != 0)) {
				{
				{
				setState(107);
				expr(0);
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(108);
					match(COMMA);
					setState(110);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__0) {
						{
						setState(109);
						match(T__0);
						}
					}

					setState(112);
					expr(0);
					}
					}
					setState(117);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralValueContext extends ParserRuleContext {
		public TerminalNode NUMERIC_LITERAL() { return getToken(TreeSQLParser.NUMERIC_LITERAL, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(TreeSQLParser.STRING_LITERAL, 0); }
		public TerminalNode JSON_TEXT_VALUE() { return getToken(TreeSQLParser.JSON_TEXT_VALUE, 0); }
		public TerminalNode RANGED_LITERAL() { return getToken(TreeSQLParser.RANGED_LITERAL, 0); }
		public TerminalNode VALUE_AT_LITERAL() { return getToken(TreeSQLParser.VALUE_AT_LITERAL, 0); }
		public TerminalNode PATH_VARIABLE() { return getToken(TreeSQLParser.PATH_VARIABLE, 0); }
		public TerminalNode NULL() { return getToken(TreeSQLParser.NULL, 0); }
		public TerminalNode TRUE() { return getToken(TreeSQLParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(TreeSQLParser.FALSE, 0); }
		public LiteralValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literalValue; }
	}

	public final LiteralValueContext literalValue() throws RecognitionException {
		LiteralValueContext _localctx = new LiteralValueContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_literalValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8478533877760L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectStmtContext extends ParserRuleContext {
		public SelectCoreContext selectCore() {
			return getRuleContext(SelectCoreContext.class,0);
		}
		public WhereExprContext whereExpr() {
			return getRuleContext(WhereExprContext.class,0);
		}
		public OrderByStmtContext orderByStmt() {
			return getRuleContext(OrderByStmtContext.class,0);
		}
		public LimitStmtContext limitStmt() {
			return getRuleContext(LimitStmtContext.class,0);
		}
		public SelectStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectStmt; }
	}

	public final SelectStmtContext selectStmt() throws RecognitionException {
		SelectStmtContext _localctx = new SelectStmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_selectStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			selectCore();
			setState(124);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(123);
				whereExpr();
				}
				break;
			}
			setState(127);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(126);
				orderByStmt();
				}
				break;
			}
			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(129);
				limitStmt();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectCoreContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(TreeSQLParser.SELECT, 0); }
		public List<ResultColumnContext> resultColumn() {
			return getRuleContexts(ResultColumnContext.class);
		}
		public ResultColumnContext resultColumn(int i) {
			return getRuleContext(ResultColumnContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(TreeSQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(TreeSQLParser.COMMA, i);
		}
		public TerminalNode FROM() { return getToken(TreeSQLParser.FROM, 0); }
		public List<JsonTextValueContext> jsonTextValue() {
			return getRuleContexts(JsonTextValueContext.class);
		}
		public JsonTextValueContext jsonTextValue(int i) {
			return getRuleContext(JsonTextValueContext.class,i);
		}
		public SelectCoreContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectCore; }
	}

	public final SelectCoreContext selectCore() throws RecognitionException {
		SelectCoreContext _localctx = new SelectCoreContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_selectCore);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(132);
			match(SELECT);
			setState(133);
			match(T__0);
			setState(134);
			resultColumn();
			setState(142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(135);
				match(COMMA);
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(136);
					match(T__0);
					}
				}

				setState(139);
				resultColumn();
				}
				}
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(155);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(145);
				match(T__0);
				setState(146);
				match(FROM);
				setState(147);
				match(T__0);
				{
				setState(148);
				jsonTextValue();
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__1 || _la==JSON_TEXT_VALUE) {
					{
					setState(150);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__1) {
						{
						setState(149);
						match(T__1);
						}
					}

					setState(152);
					jsonTextValue();
					}
				}

				}
				}
				break;
			}
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhereExprContext extends ParserRuleContext {
		public TerminalNode WHERE() { return getToken(TreeSQLParser.WHERE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public WhereExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereExpr; }
	}

	public final WhereExprContext whereExpr() throws RecognitionException {
		WhereExprContext _localctx = new WhereExprContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_whereExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			match(T__0);
			setState(158);
			match(WHERE);
			setState(159);
			match(T__0);
			setState(160);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JsonTextValueContext extends ParserRuleContext {
		public TerminalNode JSON_TEXT_VALUE() { return getToken(TreeSQLParser.JSON_TEXT_VALUE, 0); }
		public JsonTextValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonTextValue; }
	}

	public final JsonTextValueContext jsonTextValue() throws RecognitionException {
		JsonTextValueContext _localctx = new JsonTextValueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_jsonTextValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			match(JSON_TEXT_VALUE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ResultColumnContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode AS() { return getToken(TreeSQLParser.AS, 0); }
		public ColumnAliasContext columnAlias() {
			return getRuleContext(ColumnAliasContext.class,0);
		}
		public ResultColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resultColumn; }
	}

	public final ResultColumnContext resultColumn() throws RecognitionException {
		ResultColumnContext _localctx = new ResultColumnContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_resultColumn);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			expr(0);
			setState(169);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				setState(165);
				match(T__0);
				setState(166);
				match(AS);
				setState(167);
				match(T__0);
				setState(168);
				columnAlias();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OrderByStmtContext extends ParserRuleContext {
		public TerminalNode ORDER_BY() { return getToken(TreeSQLParser.ORDER_BY, 0); }
		public OrderingTermContext orderingTerm() {
			return getRuleContext(OrderingTermContext.class,0);
		}
		public OrderByStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderByStmt; }
	}

	public final OrderByStmtContext orderByStmt() throws RecognitionException {
		OrderByStmtContext _localctx = new OrderByStmtContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_orderByStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			match(T__0);
			setState(172);
			match(ORDER_BY);
			setState(173);
			match(T__0);
			setState(174);
			orderingTerm();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LimitStmtContext extends ParserRuleContext {
		public TerminalNode LIMIT() { return getToken(TreeSQLParser.LIMIT, 0); }
		public TerminalNode NUMERIC_LITERAL() { return getToken(TreeSQLParser.NUMERIC_LITERAL, 0); }
		public LimitStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limitStmt; }
	}

	public final LimitStmtContext limitStmt() throws RecognitionException {
		LimitStmtContext _localctx = new LimitStmtContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_limitStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			match(T__0);
			setState(177);
			match(LIMIT);
			setState(178);
			match(T__0);
			setState(179);
			match(NUMERIC_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OrderingTermContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AscOrDescContext ascOrDesc() {
			return getRuleContext(AscOrDescContext.class,0);
		}
		public OrderingTermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderingTerm; }
	}

	public final OrderingTermContext orderingTerm() throws RecognitionException {
		OrderingTermContext _localctx = new OrderingTermContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_orderingTerm);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			expr(0);
			setState(184);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(182);
				match(T__0);
				setState(183);
				ascOrDesc();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AscOrDescContext extends ParserRuleContext {
		public TerminalNode ASC() { return getToken(TreeSQLParser.ASC, 0); }
		public TerminalNode DESC() { return getToken(TreeSQLParser.DESC, 0); }
		public AscOrDescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ascOrDesc; }
	}

	public final AscOrDescContext ascOrDesc() throws RecognitionException {
		AscOrDescContext _localctx = new AscOrDescContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ascOrDesc);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			_la = _input.LA(1);
			if ( !(_la==ASC || _la==DESC) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColumnAliasContext extends ParserRuleContext {
		public TerminalNode STRING_LITERAL() { return getToken(TreeSQLParser.STRING_LITERAL, 0); }
		public ColumnAliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnAlias; }
	}

	public final ColumnAliasContext columnAlias() throws RecognitionException {
		ColumnAliasContext _localctx = new ColumnAliasContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_columnAlias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			match(STRING_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionNameContext extends ParserRuleContext {
		public TerminalNode FUNC_NAME() { return getToken(TreeSQLParser.FUNC_NAME, 0); }
		public FunctionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionName; }
	}

	public final FunctionNameContext functionName() throws RecognitionException {
		FunctionNameContext _localctx = new FunctionNameContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_functionName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(FUNC_NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 9);
		case 1:
			return precpred(_ctx, 8);
		case 2:
			return precpred(_ctx, 7);
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001*\u00c1\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0003\u0000/\b\u0000\u0001\u0000\u0001\u0000"+
		"\u0003\u00003\b\u0000\u0001\u0000\u0001\u0000\u0003\u00007\b\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0003\u0000<\b\u0000\u0001\u0000\u0001"+
		"\u0000\u0003\u0000@\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003"+
		"\u0000E\b\u0000\u0001\u0000\u0001\u0000\u0003\u0000I\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0003\u0000N\b\u0000\u0001\u0000\u0001\u0000"+
		"\u0003\u0000R\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000"+
		"W\b\u0000\u0001\u0000\u0001\u0000\u0003\u0000[\b\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0003\u0000`\b\u0000\u0001\u0000\u0001\u0000\u0003"+
		"\u0000d\b\u0000\u0001\u0000\u0005\u0000g\b\u0000\n\u0000\f\u0000j\t\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001o\b\u0001\u0001\u0001"+
		"\u0005\u0001r\b\u0001\n\u0001\f\u0001u\t\u0001\u0003\u0001w\b\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0003\u0003}\b\u0003\u0001"+
		"\u0003\u0003\u0003\u0080\b\u0003\u0001\u0003\u0003\u0003\u0083\b\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004"+
		"\u008a\b\u0004\u0001\u0004\u0005\u0004\u008d\b\u0004\n\u0004\f\u0004\u0090"+
		"\t\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003"+
		"\u0004\u0097\b\u0004\u0001\u0004\u0003\u0004\u009a\b\u0004\u0003\u0004"+
		"\u009c\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0003\u0007\u00aa\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\n\u0003\n\u00b9\b\n\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001"+
		"\r\u0001\r\u0000\u0001\u0000\u000e\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u0000\u0006\u0002\u0000\u0006\u0006"+
		"\t\n\u0001\u0000\u0007\b\u0001\u0000\u000b\u0010\u0002\u0000\u0016\u0017"+
		"\u0019\u001b\u0004\u0000\u001c\u001c!\"$%\'*\u0001\u0000\u0013\u0014\u00d5"+
		"\u0000.\u0001\u0000\u0000\u0000\u0002v\u0001\u0000\u0000\u0000\u0004x"+
		"\u0001\u0000\u0000\u0000\u0006z\u0001\u0000\u0000\u0000\b\u0084\u0001"+
		"\u0000\u0000\u0000\n\u009d\u0001\u0000\u0000\u0000\f\u00a2\u0001\u0000"+
		"\u0000\u0000\u000e\u00a4\u0001\u0000\u0000\u0000\u0010\u00ab\u0001\u0000"+
		"\u0000\u0000\u0012\u00b0\u0001\u0000\u0000\u0000\u0014\u00b5\u0001\u0000"+
		"\u0000\u0000\u0016\u00ba\u0001\u0000\u0000\u0000\u0018\u00bc\u0001\u0000"+
		"\u0000\u0000\u001a\u00be\u0001\u0000\u0000\u0000\u001c\u001d\u0006\u0000"+
		"\uffff\uffff\u0000\u001d/\u0003\u0004\u0002\u0000\u001e\u001f\u0005\u001b"+
		"\u0000\u0000\u001f \u0005\u0001\u0000\u0000 /\u0003\u0000\u0000\n!\"\u0003"+
		"\u001a\r\u0000\"#\u0005\u0003\u0000\u0000#$\u0003\u0002\u0001\u0000$%"+
		"\u0005\u0004\u0000\u0000%/\u0001\u0000\u0000\u0000&\'\u0003\u001a\r\u0000"+
		"\'(\u0005\u0003\u0000\u0000()\u0005\u0004\u0000\u0000)/\u0001\u0000\u0000"+
		"\u0000*+\u0005\u0003\u0000\u0000+,\u0003\u0000\u0000\u0000,-\u0005\u0004"+
		"\u0000\u0000-/\u0001\u0000\u0000\u0000.\u001c\u0001\u0000\u0000\u0000"+
		".\u001e\u0001\u0000\u0000\u0000.!\u0001\u0000\u0000\u0000.&\u0001\u0000"+
		"\u0000\u0000.*\u0001\u0000\u0000\u0000/h\u0001\u0000\u0000\u000002\n\t"+
		"\u0000\u000013\u0005\u0001\u0000\u000021\u0001\u0000\u0000\u000023\u0001"+
		"\u0000\u0000\u000034\u0001\u0000\u0000\u000046\u0007\u0000\u0000\u0000"+
		"57\u0005\u0001\u0000\u000065\u0001\u0000\u0000\u000067\u0001\u0000\u0000"+
		"\u000078\u0001\u0000\u0000\u00008g\u0003\u0000\u0000\n9;\n\b\u0000\u0000"+
		":<\u0005\u0001\u0000\u0000;:\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000"+
		"\u0000<=\u0001\u0000\u0000\u0000=?\u0007\u0001\u0000\u0000>@\u0005\u0001"+
		"\u0000\u0000?>\u0001\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0001"+
		"\u0000\u0000\u0000Ag\u0003\u0000\u0000\tBD\n\u0007\u0000\u0000CE\u0005"+
		"\u0001\u0000\u0000DC\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000"+
		"EF\u0001\u0000\u0000\u0000FH\u0007\u0002\u0000\u0000GI\u0005\u0001\u0000"+
		"\u0000HG\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000IJ\u0001\u0000"+
		"\u0000\u0000Jg\u0003\u0000\u0000\bKM\n\u0006\u0000\u0000LN\u0005\u0001"+
		"\u0000\u0000ML\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000NO\u0001"+
		"\u0000\u0000\u0000OQ\u0007\u0003\u0000\u0000PR\u0005\u0001\u0000\u0000"+
		"QP\u0001\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000"+
		"\u0000Sg\u0003\u0000\u0000\u0007TV\n\u0005\u0000\u0000UW\u0005\u0001\u0000"+
		"\u0000VU\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000WX\u0001\u0000"+
		"\u0000\u0000XZ\u0005\u0011\u0000\u0000Y[\u0005\u0001\u0000\u0000ZY\u0001"+
		"\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000"+
		"\\g\u0003\u0000\u0000\u0006]_\n\u0004\u0000\u0000^`\u0005\u0001\u0000"+
		"\u0000_^\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`a\u0001\u0000"+
		"\u0000\u0000ac\u0005\u001d\u0000\u0000bd\u0005\u0001\u0000\u0000cb\u0001"+
		"\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000"+
		"eg\u0003\u0000\u0000\u0005f0\u0001\u0000\u0000\u0000f9\u0001\u0000\u0000"+
		"\u0000fB\u0001\u0000\u0000\u0000fK\u0001\u0000\u0000\u0000fT\u0001\u0000"+
		"\u0000\u0000f]\u0001\u0000\u0000\u0000gj\u0001\u0000\u0000\u0000hf\u0001"+
		"\u0000\u0000\u0000hi\u0001\u0000\u0000\u0000i\u0001\u0001\u0000\u0000"+
		"\u0000jh\u0001\u0000\u0000\u0000ks\u0003\u0000\u0000\u0000ln\u0005\u0005"+
		"\u0000\u0000mo\u0005\u0001\u0000\u0000nm\u0001\u0000\u0000\u0000no\u0001"+
		"\u0000\u0000\u0000op\u0001\u0000\u0000\u0000pr\u0003\u0000\u0000\u0000"+
		"ql\u0001\u0000\u0000\u0000ru\u0001\u0000\u0000\u0000sq\u0001\u0000\u0000"+
		"\u0000st\u0001\u0000\u0000\u0000tw\u0001\u0000\u0000\u0000us\u0001\u0000"+
		"\u0000\u0000vk\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000w\u0003"+
		"\u0001\u0000\u0000\u0000xy\u0007\u0004\u0000\u0000y\u0005\u0001\u0000"+
		"\u0000\u0000z|\u0003\b\u0004\u0000{}\u0003\n\u0005\u0000|{\u0001\u0000"+
		"\u0000\u0000|}\u0001\u0000\u0000\u0000}\u007f\u0001\u0000\u0000\u0000"+
		"~\u0080\u0003\u0010\b\u0000\u007f~\u0001\u0000\u0000\u0000\u007f\u0080"+
		"\u0001\u0000\u0000\u0000\u0080\u0082\u0001\u0000\u0000\u0000\u0081\u0083"+
		"\u0003\u0012\t\u0000\u0082\u0081\u0001\u0000\u0000\u0000\u0082\u0083\u0001"+
		"\u0000\u0000\u0000\u0083\u0007\u0001\u0000\u0000\u0000\u0084\u0085\u0005"+
		"\u001f\u0000\u0000\u0085\u0086\u0005\u0001\u0000\u0000\u0086\u008e\u0003"+
		"\u000e\u0007\u0000\u0087\u0089\u0005\u0005\u0000\u0000\u0088\u008a\u0005"+
		"\u0001\u0000\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u0089\u008a\u0001"+
		"\u0000\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u008d\u0003"+
		"\u000e\u0007\u0000\u008c\u0087\u0001\u0000\u0000\u0000\u008d\u0090\u0001"+
		"\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008e\u008f\u0001"+
		"\u0000\u0000\u0000\u008f\u009b\u0001\u0000\u0000\u0000\u0090\u008e\u0001"+
		"\u0000\u0000\u0000\u0091\u0092\u0005\u0001\u0000\u0000\u0092\u0093\u0005"+
		"\u0015\u0000\u0000\u0093\u0094\u0005\u0001\u0000\u0000\u0094\u0099\u0003"+
		"\f\u0006\u0000\u0095\u0097\u0005\u0002\u0000\u0000\u0096\u0095\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0098\u0001\u0000"+
		"\u0000\u0000\u0098\u009a\u0003\f\u0006\u0000\u0099\u0096\u0001\u0000\u0000"+
		"\u0000\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u009c\u0001\u0000\u0000"+
		"\u0000\u009b\u0091\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000"+
		"\u0000\u009c\t\u0001\u0000\u0000\u0000\u009d\u009e\u0005\u0001\u0000\u0000"+
		"\u009e\u009f\u0005 \u0000\u0000\u009f\u00a0\u0005\u0001\u0000\u0000\u00a0"+
		"\u00a1\u0003\u0000\u0000\u0000\u00a1\u000b\u0001\u0000\u0000\u0000\u00a2"+
		"\u00a3\u0005\'\u0000\u0000\u00a3\r\u0001\u0000\u0000\u0000\u00a4\u00a9"+
		"\u0003\u0000\u0000\u0000\u00a5\u00a6\u0005\u0001\u0000\u0000\u00a6\u00a7"+
		"\u0005\u0012\u0000\u0000\u00a7\u00a8\u0005\u0001\u0000\u0000\u00a8\u00aa"+
		"\u0003\u0018\f\u0000\u00a9\u00a5\u0001\u0000\u0000\u0000\u00a9\u00aa\u0001"+
		"\u0000\u0000\u0000\u00aa\u000f\u0001\u0000\u0000\u0000\u00ab\u00ac\u0005"+
		"\u0001\u0000\u0000\u00ac\u00ad\u0005\u001e\u0000\u0000\u00ad\u00ae\u0005"+
		"\u0001\u0000\u0000\u00ae\u00af\u0003\u0014\n\u0000\u00af\u0011\u0001\u0000"+
		"\u0000\u0000\u00b0\u00b1\u0005\u0001\u0000\u0000\u00b1\u00b2\u0005\u0018"+
		"\u0000\u0000\u00b2\u00b3\u0005\u0001\u0000\u0000\u00b3\u00b4\u0005$\u0000"+
		"\u0000\u00b4\u0013\u0001\u0000\u0000\u0000\u00b5\u00b8\u0003\u0000\u0000"+
		"\u0000\u00b6\u00b7\u0005\u0001\u0000\u0000\u00b7\u00b9\u0003\u0016\u000b"+
		"\u0000\u00b8\u00b6\u0001\u0000\u0000\u0000\u00b8\u00b9\u0001\u0000\u0000"+
		"\u0000\u00b9\u0015\u0001\u0000\u0000\u0000\u00ba\u00bb\u0007\u0005\u0000"+
		"\u0000\u00bb\u0017\u0001\u0000\u0000\u0000\u00bc\u00bd\u0005%\u0000\u0000"+
		"\u00bd\u0019\u0001\u0000\u0000\u0000\u00be\u00bf\u0005#\u0000\u0000\u00bf"+
		"\u001b\u0001\u0000\u0000\u0000\u001c.26;?DHMQVZ_cfhnsv|\u007f\u0082\u0089"+
		"\u008e\u0096\u0099\u009b\u00a9\u00b8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}