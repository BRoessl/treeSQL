// Generated from /workspaces/treeSQL/src/main/antlr4/io/broessl/treesql/grammar/RJsonPointer.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RJsonPointerLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, NUMERIC_LITERAL=8, 
		RANGED_LITERAL=9, TILDE_PLUS_LITERAL=10, DOTDOTTILDE_PLUS_LITERAL=11, 
		ANON_FORWARD_RANGE=12, SLASH=13, ANON_BACKWARD_RANGE=14;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "NUMERIC_LITERAL", 
			"DIGIT", "RANGED_LITERAL", "TILDE_PLUS_LITERAL", "DOTDOTTILDE_PLUS_LITERAL", 
			"ANON_FORWARD_RANGE", "SLASH", "ANON_BACKWARD_RANGE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'~0'", "'~1'", "'{'", "','", "'}~'", "'['", "']~'", null, null, 
			null, null, "'~'", "'/'", "'..~'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "NUMERIC_LITERAL", "RANGED_LITERAL", 
			"TILDE_PLUS_LITERAL", "DOTDOTTILDE_PLUS_LITERAL", "ANON_FORWARD_RANGE", 
			"SLASH", "ANON_BACKWARD_RANGE"
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


	public RJsonPointerLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "RJsonPointer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u000e[\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0003\u00073\b\u0007\u0001\u0007\u0004\u00076\b\u0007\u000b\u0007"+
		"\f\u00077\u0001\u0007\u0001\u0007\u0005\u0007<\b\u0007\n\u0007\f\u0007"+
		"?\t\u0007\u0003\u0007A\b\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0004\t"+
		"G\b\t\u000b\t\f\tH\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0000\u0000\u000f\u0001"+
		"\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007"+
		"\u000f\b\u0011\u0000\u0013\t\u0015\n\u0017\u000b\u0019\f\u001b\r\u001d"+
		"\u000e\u0001\u0000\u0003\u0001\u000009\u0002\u0000AZaz\u0004\u000009A"+
		"Z__az^\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000"+
		"\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000"+
		"\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000"+
		"\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000"+
		"\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000"+
		"\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000"+
		"\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0001"+
		"\u001f\u0001\u0000\u0000\u0000\u0003\"\u0001\u0000\u0000\u0000\u0005%"+
		"\u0001\u0000\u0000\u0000\u0007\'\u0001\u0000\u0000\u0000\t)\u0001\u0000"+
		"\u0000\u0000\u000b,\u0001\u0000\u0000\u0000\r.\u0001\u0000\u0000\u0000"+
		"\u000f2\u0001\u0000\u0000\u0000\u0011B\u0001\u0000\u0000\u0000\u0013D"+
		"\u0001\u0000\u0000\u0000\u0015J\u0001\u0000\u0000\u0000\u0017M\u0001\u0000"+
		"\u0000\u0000\u0019S\u0001\u0000\u0000\u0000\u001bU\u0001\u0000\u0000\u0000"+
		"\u001dW\u0001\u0000\u0000\u0000\u001f \u0005~\u0000\u0000 !\u00050\u0000"+
		"\u0000!\u0002\u0001\u0000\u0000\u0000\"#\u0005~\u0000\u0000#$\u00051\u0000"+
		"\u0000$\u0004\u0001\u0000\u0000\u0000%&\u0005{\u0000\u0000&\u0006\u0001"+
		"\u0000\u0000\u0000\'(\u0005,\u0000\u0000(\b\u0001\u0000\u0000\u0000)*"+
		"\u0005}\u0000\u0000*+\u0005~\u0000\u0000+\n\u0001\u0000\u0000\u0000,-"+
		"\u0005[\u0000\u0000-\f\u0001\u0000\u0000\u0000./\u0005]\u0000\u0000/0"+
		"\u0005~\u0000\u00000\u000e\u0001\u0000\u0000\u000013\u0005-\u0000\u0000"+
		"21\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000035\u0001\u0000\u0000"+
		"\u000046\u0003\u0011\b\u000054\u0001\u0000\u0000\u000067\u0001\u0000\u0000"+
		"\u000075\u0001\u0000\u0000\u000078\u0001\u0000\u0000\u00008@\u0001\u0000"+
		"\u0000\u00009=\u0005.\u0000\u0000:<\u0003\u0011\b\u0000;:\u0001\u0000"+
		"\u0000\u0000<?\u0001\u0000\u0000\u0000=;\u0001\u0000\u0000\u0000=>\u0001"+
		"\u0000\u0000\u0000>A\u0001\u0000\u0000\u0000?=\u0001\u0000\u0000\u0000"+
		"@9\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000A\u0010\u0001\u0000"+
		"\u0000\u0000BC\u0007\u0000\u0000\u0000C\u0012\u0001\u0000\u0000\u0000"+
		"DF\u0007\u0001\u0000\u0000EG\u0007\u0002\u0000\u0000FE\u0001\u0000\u0000"+
		"\u0000GH\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000HI\u0001\u0000"+
		"\u0000\u0000I\u0014\u0001\u0000\u0000\u0000JK\u0005~\u0000\u0000KL\u0003"+
		"\u0013\t\u0000L\u0016\u0001\u0000\u0000\u0000MN\u0005.\u0000\u0000NO\u0005"+
		".\u0000\u0000OP\u0005~\u0000\u0000PQ\u0001\u0000\u0000\u0000QR\u0003\u0013"+
		"\t\u0000R\u0018\u0001\u0000\u0000\u0000ST\u0005~\u0000\u0000T\u001a\u0001"+
		"\u0000\u0000\u0000UV\u0005/\u0000\u0000V\u001c\u0001\u0000\u0000\u0000"+
		"WX\u0005.\u0000\u0000XY\u0005.\u0000\u0000YZ\u0005~\u0000\u0000Z\u001e"+
		"\u0001\u0000\u0000\u0000\u0006\u000027=@H\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}