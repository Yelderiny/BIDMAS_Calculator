import java.util.Scanner;

public class CalculatorTester
{
    public static void main(String[] args)
    {
        Calculator operation = new Calculator();
        Scanner in = new Scanner(System.in);
        boolean done = false;

        while (!done)
        {
            System.out.print("> ");
            String currOperation = in.nextLine();
            currOperation = currOperation.toLowerCase();

            switch (currOperation)
            {
                //exit
                case "x":
                    done = true;
                    break;

                //print saved value
                case "mr":
                    System.out.println(operation.getMemoryValue());
                    break;

                //clear memory
                case "c":
                    operation.clearMemory();
                    break;

                case "h":
                    operation.printHistory();
                    break;

                default:
                    float answer = operation.evaluate(currOperation);

                    if (answer != Float.MIN_VALUE) System.out.println(operation.expressionReformulater(currOperation) + " = " + answer);
                    else System.out.println("Invalid input");
            }
        }
    }
}
