package frontend.pascal.tokens.parsers;

import frontend.Token;
import frontend.TokenType;
import frontend.pascal.PascalParserTD;
import frontend.pascal.PascalTokenType;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;
import intermediate.ICodeNodeType;
import intermediate.icodeimpl.ICodeNodeTypeImpl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static frontend.pascal.PascalErrorCode.*;
import static frontend.pascal.PascalTokenType.*;
import static intermediate.icodeimpl.ICodeKeyImpl.ID;
import static intermediate.icodeimpl.ICodeKeyImpl.VALUE;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.*;

public class ExpressionParser extends StatementParser {
    private static final EnumSet<PascalTokenType> REL_OPS = EnumSet.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_EQUALS, GREATER_THAN, GREATER_EQUALS
    );
    private static final Map<PascalTokenType, ICodeNodeType> REL_OPS_MAP = new HashMap<>();

    static {
        REL_OPS_MAP.put(EQUALS, EQ);
        REL_OPS_MAP.put(NOT_EQUALS, NE);
        REL_OPS_MAP.put(LESS_THAN, LT);
        REL_OPS_MAP.put(LESS_EQUALS, LE);
        REL_OPS_MAP.put(GREATER_THAN, GT);
        REL_OPS_MAP.put(GREATER_EQUALS, GE);
    }

    private static final EnumSet<PascalTokenType> ADD_OPS = EnumSet.of(
            PLUS, MINUS, PascalTokenType.OR
    );
    private static final Map<PascalTokenType, ICodeNodeTypeImpl> ADD_OPS_MAP = new HashMap<>();

    static {
        ADD_OPS_MAP.put(PLUS, ADD);
        ADD_OPS_MAP.put(MINUS, SUBTRACT);
        ADD_OPS_MAP.put(PascalTokenType.OR, ICodeNodeTypeImpl.OR);
    }

    private static final EnumSet<PascalTokenType> MULT_OPS = EnumSet.of(
            STAR, SLASH, DIV, PascalTokenType.MOD, PascalTokenType.AND
    );
    private static final Map<PascalTokenType, ICodeNodeTypeImpl> MULT_OPS_MAP = new HashMap<>();

    static {
        MULT_OPS_MAP.put(STAR, MULTIPLY);
        MULT_OPS_MAP.put(SLASH, FLOAT_DIVIDE);
        MULT_OPS_MAP.put(DIV, INTEGER_DIVIDE);
        MULT_OPS_MAP.put(PascalTokenType.MOD, ICodeNodeTypeImpl.MOD);
        MULT_OPS_MAP.put(PascalTokenType.AND, ICodeNodeTypeImpl.AND);
    }

    public ExpressionParser(PascalParserTD parser) {
        super(parser);
    }

    public ICodeNode parse(Token token) throws Exception {
        return parseExpression(token);
    }

    private ICodeNode parseExpression(Token token) throws Exception {
        var rootNode = parseSimpleExpression(token);

        token = currentToken();
        var tokenType = token.getType();

        if (REL_OPS.contains(tokenType)) {
            var nodeType = REL_OPS_MAP.get(tokenType);
            var operatorNode = ICodeFactory.createICodeNode(nodeType);
            operatorNode.addChild(rootNode);

            token = nextToken();
            tokenType = token.getType();

            operatorNode.addChild(parseSimpleExpression(token));

            rootNode = operatorNode;
        }

        return rootNode;
    }

    private ICodeNode parseSimpleExpression(Token token) throws Exception {
        var signType = (TokenType) null;

        var tokenType = token.getType();
        if (tokenType == PLUS || tokenType == MINUS) {
            signType = tokenType;
            token = nextToken();
        }

        var rootNode = parseTerm(token);

        if (signType == MINUS) {
            var negateNode = ICodeFactory.createICodeNode(NEGATE);
            negateNode.addChild(rootNode);
            rootNode = negateNode;
        }

        token = currentToken();
        tokenType = token.getType();

        while (ADD_OPS.contains(tokenType)) {
            var nodeType = ADD_OPS_MAP.get(tokenType);
            var operatorNode = ICodeFactory.createICodeNode(nodeType);
            operatorNode.addChild(rootNode);

            token = nextToken();

            operatorNode.addChild(parseTerm(token));

            rootNode = operatorNode;

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }

    private ICodeNode parseTerm(Token token) throws Exception {
        var rootNode = parseFactor(token);

        token = currentToken();
        var tokenType = token.getType();

        while (MULT_OPS.contains(tokenType)) {
            var nodeType = MULT_OPS_MAP.get(tokenType);
            var operatorNode = ICodeFactory.createICodeNode(nodeType);
            operatorNode.addChild(rootNode);

            token = nextToken();
            tokenType = token.getType();

            operatorNode.addChild(parseFactor(token));

            rootNode = operatorNode;

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }

    private ICodeNode parseFactor(Token token) throws Exception {
        var tokenType = token.getType();
        var rootNode = (ICodeNode) null;

        switch ((PascalTokenType) tokenType) {
            case IDENTIFIER: {
                var name = token.getText().toLowerCase();
                var id = symTabStack.lookup(name);
                if (id == null) {
                    errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
                    id = symTabStack.enterLocal(name);
                }

                rootNode = ICodeFactory.createICodeNode(VARIABLE);
                rootNode.setAttribute(ID, id);
                id.appendLineNumber(token.getLineNumber());

                token = nextToken();
                break;
            }

            case INTEGER: {
                rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();
                break;
            }

            case REAL: {
                rootNode = ICodeFactory.createICodeNode(REAL_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();
                break;
            }

            case STRING: {
                rootNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();
                break;
            }

            case NOT: {
                token = nextToken();

                rootNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);
                rootNode.addChild(parseFactor(token));

                break;
            }

            case LEFT_PAREN: {
                token = nextToken();
                rootNode = parseExpression(token);

                token = currentToken();
                if (token.getType() == RIGHT_PAREN) {
                    token = nextToken();
                } else {
                    errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
                }

                break;
            }

            default: {
                errorHandler.flag(token, UNEXPECTED_TOKEN, this);
                break;
            }
        }

        return rootNode;
    }
}
