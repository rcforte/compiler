package frontend.pascal.tokens.parsers;

import frontend.Token;
import frontend.pascal.PascalParserTD;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;

import static frontend.pascal.PascalErrorCode.MISSING_COLON_EQUALS;
import static frontend.pascal.PascalTokenType.COLON_EQUALS;
import static intermediate.icodeimpl.ICodeKeyImpl.ID;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.ASSIGN;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.VARIABLE;

public class AssignmentStatementParser extends StatementParser {
    public AssignmentStatementParser(PascalParserTD parser) {
        super(parser);
    }

    public ICodeNode parse(Token token) throws Exception {
        var assignNode = ICodeFactory.createICodeNode(ASSIGN);

        var targetName = token.getText().toLowerCase();
        var targetId = symTabStack.lookup(targetName);
        if (targetId == null) targetId = symTabStack.enterLocal(targetName);

        targetId.appendLineNumber(token.getLineNumber());

        token = nextToken();

        var variableNode = ICodeFactory.createICodeNode(VARIABLE);
        variableNode.setAttribute(ID, targetId);

        assignNode.addChild(variableNode);

        if (token.getType() == COLON_EQUALS) {
            token = nextToken();
        } else {
            errorHandler.flag(token, MISSING_COLON_EQUALS, this);
        }

        var expressionParser = new ExpressionParser(this);
        assignNode.addChild(expressionParser.parse(token));
        return assignNode;
    }
}
