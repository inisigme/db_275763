package pl.edu.agh.tkk17.sample;

import java.util.Arrays;
import java.util.Iterator;

import java.util.Stack;

public class Parser
{
    private Iterator<Token> tokens;
    private Token ctoken;

    private Stack<Token> brackets;

    public Parser(Iterable<Token> tokens)
    {
        this.brackets = new Stack<Token>();
        this.tokens = tokens.iterator();
        this.forward();

    }

    private void forward()
    {
        this.ctoken = this.tokens.next();
    }

    private String value()
    {
        return this.ctoken.getValue();
    }

    private boolean check(TokenType type)
    {
        return this.ctoken.getType() == type;
    }

    private void expect(TokenType type)
    {
        if (!this.check(type)) {
            throw new UnexpectedTokenException(this.ctoken, type);
        }
    }

    private Node parseNumber()
    {
        this.expect(TokenType.NUM);
        String value = this.value();
        this.forward();
        return new NodeNumber(value);
    }

    private Node parseTerm()
    {
        Node left;
        if( this.checkLeftBracket()){
            left = this.parseExpression();
        } else {
            left = this.parseNumber();
        }


        if ( this.check(TokenType.MUL)) {
            this.forward();
            Node right = this.parseTerm();
            return new NodeMul(left, right);
        } else if ( this.check(TokenType.DIV)) {
            this.forward();
            Node right = this.parseTerm();
            return new NodeDiv(left, right);
        } else {
            checkRightBracket();
            return left;
        }
    }

    private Node parseExpression()
    {
        Node left = this.parseTerm();
        if (this.check(TokenType.ADD)) {
            this.forward();
            Node right = this.parseExpression();
            return new NodeAdd(left, right);
        } else if ( this.check(TokenType.SUB) ) {
            this.forward();
            Node right = this.parseExpression();
            return new NodeSub(left, right);
        } else {
            return left;
        }
    }

    private boolean checkLeftBracket() {
        if (this.check( TokenType.LBR)){
            this.brackets.push( this.ctoken);
            this.forward();
            return true;
        }
        return false;
    }

    private boolean checkRightBracket() {
        if ( this.check(TokenType.RBR)) {
            if( this.brackets.empty() || this.brackets.peek().getType() == TokenType.RBR) {
                throw new UnexpectedTokenException(this.ctoken);
            }
            this.brackets.pop();
            this.forward();
            return true;
        }



        return false;
    }

    private Node parseProgram()
    {
        Node root = this.parseExpression();
        if( this.brackets.size() != 0) throw new UnexpectedTokenException( this.ctoken);
        this.expect(TokenType.END);
        return root;
    }

    public static Node parse(Iterable<Token> tokens)
    {
        Parser parser = new Parser(tokens);
        Node root = parser.parseProgram();
        return root;
    }
}
