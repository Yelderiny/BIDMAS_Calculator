/*
 * @author: yelderiny
 * Don't steal my code ReEeEeEeEm #plagirism
 */


/*
 * ACTIONABLE ITEMS:
 *
 * write a method expressionOutputer that prints out the input expression in a viewable form rather than the form that methods use to compute it (make expression reformulator private after doing this)
 * expand the expontential functionality to be able to account for things like 2^(1/2) it occurs to me that the best way to do this is to call the part of the extendedEvaluator helper that deals with division etc. from the part that deals with exponentials. it becomes more recursive.
 * contemplate removing the useless line that my expressionReformulator fixes for me automatically
 * contemplate the use of getHistoryValue(int index) in my code
 */

import java.util.ArrayList;

public class Calculator
{
    //fields
    private String operand1, operand2, operator;
    private float memoryValue, result;
    private final ArrayList<Float> history = new ArrayList<>();

    //constructor
    public Calculator() { resetStrings(); }

    //accessors
    public String getOperand1() { return operand1; }
    public String getOperand2() { return operand2; }
    public String getOperator() { return operator; }
    public float getResult() { return result; }
    public float getMemoryValue()
    {
        if (getResult() == 0) return 0;
        else return memoryValue;
    }

    public float getHistoryValue(int index) { return (float) history.toArray()[index]; }

    //mutators
    public void setOperand1(String str) { operand1 = str; }
    public void setOperand2(String str) { operand2 = str; }
    public void setOperator(String str) { operator = str; }
    public void setResult(final float num) { result = num; }
    public void setMemoryValue(final float memval) { memoryValue = memval; }
    public void clearMemory() { memoryValue = 0; }
    private void resetStrings()
    {
        setOperand1("");
        setOperand2("");
        setOperator("");
    }

    private void setHistory() { history.add(getResult()); }
    private void deleteHistory() { history.remove(history.toArray().length - 1); }

    //printers
    public void printHistory()
    {
        for ( int i = 0; i<history.toArray().length; i++)
        {
            System.out.print(history.toArray()[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    //validators
    private static boolean isNumber(final String expression)
    {
        if (expression == null) return false;

        try { Float.parseFloat(expression); }
        catch (NumberFormatException nfe) { return false; }

        return true;
    }

    private boolean isOperator(final char c) { return c == '+' || c == '-' || c == '*' || c == '/' || c == '^'; }
    private boolean isOperator(final String expression) { return expression.equals("+") || expression.equals("-") || expression.equals("*") || expression.equals("/") || expression.equals("^"); }
    private boolean isSymbol(final char c) { return isOperator(c) || c == '(' || c == ')'; }
    private boolean isSymbol(final String expression) { return isOperator(expression) || expression.equals("(") || expression.equals(")"); }

    //reformulates the input
    //make private after creating the expression outputer
    public String reformatExpression(final String expression)
    {
        var newExpression = new StringBuilder(" "); //initialize StringBuilder
        char[] arr = expression.toCharArray(); //convert to char array

        //iterate
        for (int i = 0; i < arr.length; i++)
        {
            //not whitespace
            if (arr[i] != ' ')
            {
                //the element is a number
                if (arr[i] >= '0' && arr[i] <= '9' || arr[i] == '.')
                {
                    newExpression.append(arr[i]); //add number to StringBuilder

                    //iterate the rest of the expression
                    for (int j = i+1; j < arr.length; j++)
                    {
                        //not whitespace
                        if (arr[j] != ' ')
                        {
                            //the element is an operator, add a space, break the loop
                            if (isSymbol(arr[j])) newExpression.append(" ");
                            break;
                        }
                    }
                }

                //the element is a minus
                else if (arr[i] == '-')
                {
                    if (i == 0) newExpression.append(arr[i]); //the first element, don't add a space
                    else
                    {
                        String[] check = newExpression.toString().split(" "); //transform current StringBuilder to String array
                        char[] check2 = check[check.length -1].toCharArray(); //transform final element of String array to char array

                        //the final element was either a singular number or an operator
                        if (check2.length == 1)
                        {
                            //char
                            if (isOperator(check2[0]) || check2[0] == '(')
                            {
                                //iterate the rest of the input string
                                for (int j = i + 1; j < arr.length; j++)
                                {
                                    //not whitespace
                                    if (arr[j] != ' ')
                                    {
                                        if (Character.isDigit(arr[j])) newExpression.append(arr[i]); //the next entry is a number, add no space, break the loop
                                        if (arr[j] == '(') newExpression.append(arr[i]).append(" "); //the next entry is an open bracket, add the minus then whitespace
                                        break;
                                    }
                                }
                            }
                            else newExpression.append(arr[i]).append(" "); //one digit number, add a space
                        }
                        else newExpression.append(arr[i]).append(" "); //longer digit numbers, add a space
                    }
                }
                else if (isSymbol(arr[i])) newExpression.append(arr[i]).append(" "); //for any other operator or bracket, add a space
                else newExpression.append(arr[i]); //otherwise, add whatever remains with no space
            }
        }
        return newExpression.toString().trim(); //return the final string
    }

    //evaluators
    public float evaluate(String expression)
    {
        String newExpression = reformatExpression(expression);

        if (!newExpression.equals(" "))
        {
            String[] arr = newExpression.split(" "); //convert input to String array
            resetStrings(); //result all strings to ""

            //at this point in time, if the user enters a stand-alone number, it should just return the number or a negative number
            if (arr.length == 1)
            {
                char[] arr2 = arr[0].toCharArray();
                if (arr2[0] == '-') return evaluateMemHelper(newExpression);
                else
                {
                    setResult(0);
                    if (isNumber(newExpression)) return Float.parseFloat(newExpression);
                    else return Float.MIN_VALUE;
                }
            }

            if (arr.length == 3) return evaluateHelper(newExpression); //length three means two operands, send to evaluateHelper



            if (isOperator(arr[0]) && arr.length == 2) return evaluateMemHelper(newExpression); //length two means using memoryValue

            return extendedEvaluatorHelper(newExpression); //otherwise evaluating more than two operands
        }
        else
        {
            setResult(0);
            return Float.MIN_VALUE;
        }
    }

    //evaluates expressions with two numbers
    private float evaluateHelper(final String expression)
    {
        String[] arr = expression.split(" ");
        var operandOne = new StringBuilder();
        var operandTwo = new StringBuilder();
        var operatorBuilder = new StringBuilder();

        for (String s : arr)
        {
            if (isNumber(s) || isSymbol(s))
            {
                if (isOperator(s)) operatorBuilder.append(s);
                else
                {
                    if (operatorBuilder.toString().isEmpty()) operandOne.append(s); //before the operator, increment operand1
                    else operandTwo.append(s); //after the operator, increment operand2
                }
            }
            else
            {
                setResult(0);
                return Float.MIN_VALUE;
            }
        }

        //assign StringBuilder to class attributes
        setOperand1(operandOne.toString());
        setOperand2(operandTwo.toString());
        setOperator(operatorBuilder.toString());

        if (getOperand1().equals("") || getOperand2().equals("") || getOperator().equals(""))
        {
            setResult(0);
            return Float.MIN_VALUE; //check that all requirements are met
        }

        //input begins with a number
        float operand1 = Float.parseFloat(getOperand1()); //convert operand1
        float operand2 = Float.parseFloat(getOperand2()); //convert operand2

        //find the operation
        switch (getOperator())
        {
            case "+":
                setResult(operand1 + operand2);
                break;

            case "-":
                setResult(operand1 - operand2);
                break;

            case "/":
                if (operand2 == 0)
                {
                    setResult(0);
                    return Float.MIN_VALUE;
                }
                setResult(operand1 / operand2);
                break;

            case "*":
                setResult(operand1 * operand2);
                break;

            case "^":
                setResult((float) Math.pow(operand1, operand2));
                break;

            default:
                setResult(0);
                return Float.MIN_VALUE;
        }
        setHistory();
        setMemoryValue(getResult());
        return getResult();
    }

    //evaluates expressions done on the memory value
    private float evaluateMemHelper(final String expression) { return evaluate(getMemoryValue() + expression); }

    //evaluates expressions with multiple operands and operators
    private float extendedEvaluatorHelper(final String expression)
    {
        boolean muldiv = false, addsub = false, bracket = false, exponential = false;
        String[] arr = expression.split(" "); //split expression
        var newExpression = new StringBuilder();

        //base case. expression is of length one means we have the result
        if (arr.length == 1)
        {
            setHistory(); //add most recent result to history
            setMemoryValue(getResult());
            return getResult();
        }

        //iterate
        for (String s : arr)
        {
            if (!isNumber(s) && !isSymbol(s)) return Float.MIN_VALUE; //error check
            if (s.equals("(")) bracket = true;
            if (s.equals("^")) exponential = true;
            if (s.equals("/") || s.equals("*")) muldiv = true; //multiplication or division exist
            if (s.equals("+") || s.equals("-")) addsub = true; //addition or subtraction exist
        }

        //brackets
        if (bracket)
        {
            for (int i = 0; i < arr.length; i++)
            {
                if (arr[i].equals("("))
                {
                    int endBracketIndex = 0;
                    StringBuilder brackets = new StringBuilder();

                    for (int j = i + 1; j < arr.length; j++)
                    {
                        if (arr[j].equals(")"))
                        {
                            endBracketIndex = j;
                            break;
                        }
                    }
                    if (endBracketIndex == 0)
                    {
                        setResult(0);
                        return Float.MIN_VALUE;
                    }

                    for (int e = i+1; e < endBracketIndex; e++) { brackets.append(arr[e]); }

                    float evaluation = evaluate(brackets.toString());

                    if (evaluation != Float.MIN_VALUE) newExpression.append(evaluation).append(" ");
                    else
                    {
                        setResult(0);
                        return Float.MIN_VALUE;
                    }

                    if (!((endBracketIndex + 1) > arr.length)) for (int j = endBracketIndex + 1; j < arr.length; j++) { newExpression.append(arr[j]).append(" "); } //add the rest of the operation to the StringBuilder

                    deleteHistory(); //delete all middle operations from history
                    return extendedEvaluatorHelper(newExpression.toString()); //call the extendedEvaluator helper on the new String
                }
                else newExpression.append(arr[i]).append(" ");
            }
        }

        //exponentials
        if (exponential)
        {
            //iterate
            for (int i = 0; i < arr.length; i++)
            {
                //exponential
                if (arr[i].equals("^"))
                {
                    char operator = '^'; //set operator
                    return expressionReducer(arr, newExpression, i, operator); //recursion
                }
                // code works without this line because of the expressionReformulator
                else if (!arr[i + 1].equals("^")) newExpression.append(arr[i]).append(" "); //add element to StringBuilder if the next string is not muldiv
            }
        }

        //multiplication or division
        if (muldiv)
        {
            //iterate
            for (int i = 0; i < arr.length; i++)
            {
                //multiplication
                if (arr[i].equals("*"))
                {
                    char operator = '*'; //set operator
                    return expressionReducer(arr, newExpression, i, operator); //recursion
                }

                //division
                if (arr[i].equals("/"))
                {
                    char operator = '/'; //set operator
                    return expressionReducer(arr, newExpression, i, operator); //recursion
                }
                // code works without this line because of the expressionReformulator
                else if (!arr[i + 1].equals("*") && !arr[i+1].equals("/")) newExpression.append(arr[i]).append(" "); //add element to StringBuilder if the next string is not muldiv
            }
        }

        //addition or subtraction
        if (addsub)
        {
            for (int i = 0; i < arr.length; i++)
            {
                //addition
                if (arr[i].equals("+"))
                {
                    char operator = '+'; //set operator
                    return expressionReducer(arr, newExpression, i, operator); //recursion
                }

                //subtraction
                if (arr[i].equals("-"))
                {
                    char operator = '-'; //set operator
                    return expressionReducer(arr, newExpression, i, operator); //recursion
                }
                // code works without this line because of the expressionReformulator
                else if (!arr[i + 1].equals("+") && !arr[i+1].equals("-")) newExpression.append(arr[i]).append(" "); //add element to StringBuilder if the next string is not addsub
            }
        }
        setResult(0);
        return Float.MIN_VALUE;
    }

    //helper function for extendedEvaluatorHelper
    private float expressionReducer(final String[] arr, final StringBuilder newExpression, final int i, final char operator)
    {
        float evaluation = evaluate(arr[i-1] + operator + arr[i + 1]);

        if (evaluation != Float.MIN_VALUE) newExpression.append(evaluation).append(" "); //evaluate expresion around operator and add result to StringBuilder
        else
        {
            setResult(0);
            return Float.MIN_VALUE;
        }
        //not the final operation in the larger operation
        if (!((i + 2) > arr.length)) for (int j = i + 2; j < arr.length; j++) { newExpression.append(arr[j]).append(" "); } //add the rest of the operation to the StringBuilder

        deleteHistory(); //delete all middle operations from history
        return extendedEvaluatorHelper(newExpression.toString()); //call the extendedEvaluator helper on the new String
    }
}

