package frontend.pascal.tokens.parsers;

import frontend.EofToken;
import frontend.Token;
import frontend.pascal.PascalErrorCode;
import frontend.pascal.PascalParserTD;
import frontend.pascal.PascalTokenType;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;

import static frontend.pascal.PascalErrorCode.MISSING_SEMICOLON;
import static frontend.pascal.PascalErrorCode.UNEXPECTED_TOKEN;
import static frontend.pascal.PascalTokenType.IDENTIFIER;
import static frontend.pascal.PascalTokenType.SEMICOLON;
import static intermediate.icodeimpl.ICodeKeyImpl.LINE;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.NO_OP;

public class StatementParser extends PascalParserTD {

    public StatementParser(PascalParserTD parser) {
        super(parser);
    }

    public ICodeNode parse(Token token) throws Exception {
        var statementNode = (ICodeNode) null;

        switch ((PascalTokenType) token.getType()) {
            case BEGIN: {
                var compoundParser = new CompoundStatementParser(this);
                statementNode = compoundParser.parse(token);
            }
            break;

            case IDENTIFIER: {
                var assignmentParser = new AssignmentStatementParser(this);
                statementNode = assignmentParser.parse(token);
            }
            break;

            default: {
                statementNode = ICodeFactory.createICodeNode(NO_OP);
            }
            break;
        }

        setLineNumber(statementNode, token);

        return statementNode;
    }

    public void setLineNumber(ICodeNode node, Token token) {
        if (node != null) {
            node.setAttribute(LINE, token.getLineNumber());
        }
    }

    public void parseList(Token token,
                          ICodeNode parentNode,
                          PascalTokenType terminator,
                          PascalErrorCode errorCode)
            throws Exception {
        while (!(token instanceof EofToken) && (token.getType() != terminator)) {
            var statementNode = parse(token);
            parentNode.addChild(statementNode);

            token = currentToken();
            var tokenType = token.getType();
            if (tokenType == SEMICOLON) {
                token = nextToken();
            } else if (tokenType == IDENTIFIER) {
                errorHandler.flag(token, MISSING_SEMICOLON, this);
            } else if (tokenType != terminator) {
                errorHandler.flag(token, UNEXPECTED_TOKEN, this);
                token = nextToken();
            }
        }

        if (token.getType() == terminator) {
            token = nextToken();
        } else {
            errorHandler.flag(token, errorCode, this);
        }
    }
}
