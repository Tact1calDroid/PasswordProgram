package Project;
import java.io.IOException;
import java.util.Scanner;
public class Main {
    private static String FactorCode;
    private static String userPassword;
    private static String emailAddress;
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to the authenticator program!\nIf you would like to generate a code, type 1.\nIf you would like to verify a code's existence, type 2.\nIf you would like to create an account, type 3.\nIf you need help with the username & password criteria, type 4.");
        System.out.print("Your input here: ");

        String userInput = input.nextLine();

        switch(userInput){
            case "1":

                twoFactorAuthentication test = new twoFactorAuthentication();
                System.out.print("\nEnter your email Address:");
                emailAddress = input.nextLine().toLowerCase();
//                test.verifyEmail(emailAddress);

                System.out.print("\nEnter your password:");
                userPassword = input.nextLine();

                test.checkEmailExist(emailAddress,userPassword);
                FactorCode = test.generateCode(emailAddress,userPassword);

                System.out.println("2fa Code: " + FactorCode);
                input.close();
                break;
            case "2":
                twoFactorAuthentication verify = new twoFactorAuthentication();
                System.out.print("\nEnter your email Address:");
                emailAddress = input.nextLine().toLowerCase();

                System.out.print("\nEnter your password:");
                userPassword = input.nextLine();

                System.out.println("\nPlease enter your code given to verify its existence:");
                System.out.print("Insert your code here: ");
                String checkCode = input.nextLine();


                verify.verifyCode(checkCode,emailAddress,userPassword);
                input.close();
                break;
            case "3":
                twoFactorAuthentication stage3 = new twoFactorAuthentication();
                System.out.println("\nWelcome to our program! Please follow the directions below");
                System.out.print("\nPlease create your email address: ");
                String userNameCheck = input.nextLine().toLowerCase();
                stage3.verifyEmail(userNameCheck);
                System.out.print("\nPlease create your password: ");
                String passWordCheck = input.nextLine();
                stage3.verifyPassword(passWordCheck);
                stage3.writeToEmailsVerified(userNameCheck,passWordCheck);
                break;
            case "4":
                twoFactorAuthentication stage4 = new twoFactorAuthentication();
                System.out.println("\nWelcome to our help center!");
                System.out.println("Type 'email' if you would like to see the criteria for email!");
                System.out.println("Type 'password' if you would like to see the criteria for password!");
                String helpOption = input.nextLine().toLowerCase();
                input.close();
                if (helpOption.equals("email")) {
                    stage4.helpInfoEmail();
                }
                else if (helpOption.equals("password")) {
                    stage4.helpInfoPassword();
                }
                else {
                    System.out.println("You did not enter any of the options.Leaving program");

                }
                break;
            default:
                System.out.println("\nYou didn't enter a valid input. The options are: 1, 2 ,3, or 4.");
        }


    }
}
