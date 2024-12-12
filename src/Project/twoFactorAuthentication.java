package Project;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class twoFactorAuthentication {
    // Create our variables
    private String code;
    private boolean isVerified;
    private String hashCode;
    private String hashCodePass;
    private String stringBasedCode;
    private String regExPattern;

    public twoFactorAuthentication() {
        this.code = "";
        this.isVerified = false;
    }

    public String getCode() {
        return code;
    }
    public void setVerified(boolean verified) {
        isVerified = verified;
    }


    public String generateCode(String userPass, String emailAddress) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int random = (int)(Math.random() * 10);
            sb.append(random);
        }
        code = sb.toString();
        writeCode(code,emailAddress,userPass);
        return code;
    }

    public void writeCode(String authenticationCode, String password, String email){
        Path fileName = Path.of("/Users/vyasgupta/Desktop/Programs/PersonalProject/src/ProjectCode.txt");
        Path hashName = Path.of("/Users/vyasgupta/Desktop/Programs/PersonalProject/src/HashCode.txt");
        try{

            DateTimeFormatter date = DateTimeFormatter.ofPattern("MM/dd/yyyy:HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            //String dateString = date.format(now);
            stringBasedCode = email + password + authenticationCode;

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            //byte[] messageDigest = md.digest(authenticationCode.getBytes());
            byte[] messageDigest = md.digest(stringBasedCode.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            hashCode = no.toString(16);

            while (hashCode.length() < 32) {
                hashCode = "0" + hashCode;
            }

            Files.writeString(hashName,  hashCode +":" +date.format(now) + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.writeString(fileName, email +":"+password +":"+ authenticationCode+":" + date.format(now) + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException | NoSuchAlgorithmException e){
            System.err.println("Error:" + e.getMessage());

        }
    }

    public void verifyCode(String userCode, String address, String inputPass) {
        //Path filePath = Paths.get("/Users/rakeshgupta/IdeaProjects/PersonalProject/src/ProjectCode.txt");
        Path filePath = Paths.get("/Users/vyasgupta/Desktop/Programs/PersonalProject/src/HashCode.txt");
        try {

            stringBasedCode = address + inputPass + userCode;
            MessageDigest messageHash = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = messageHash.digest(stringBasedCode.getBytes());
            BigInteger num = new BigInteger(1,messageDigest);
            hashCode = num.toString(16);

            while (hashCode.length() < 32) {
                hashCode = "0" + hashCode;
            }

            List<String> lines = Files.readAllLines(filePath);
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                if (line.contains(hashCode)){
                    System.out.println("\nHash has been located");
                    System.out.println("Checking if it meets time criteria.");

                    String[] parts = line.split(":");
                    String dateTimeString = parts[1] + ":" + parts[2] + ":" + parts[3] + ":" + parts[4];

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy:HH:mm:ss");
                    LocalDateTime codeDateTime = LocalDateTime.parse(dateTimeString, formatter);

                    LocalDateTime now = LocalDateTime.now();
                    Duration duration = Duration.between(codeDateTime, now);


                    if (duration.toMinutes() <= 5) {
                        System.out.println("\nYou are within the time limit. Proceed.");
                        isVerified = true;
                    } else {
                        System.out.println("\nThe time limit on the code has expired. Exiting");
                        isVerified = true;
                    }
                } else {
                    updatedLines.add(line);
                }
            }

            if (isVerified) {
                Files.write(filePath, updatedLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            isVerified = false;
            System.err.println(e.getMessage());
        }
    }


    public void verifyEmail(String email){
        //System.out.println("\nVerify email address with format criteria");
        regExPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regExPattern);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()){
            System.out.println("\nEmail has meet the format criteria");
        } else{
            System.out.println("\nEmail does not meet the format criteria");
            helpInfoEmail();
            System.exit(1);
        }



    }
    public void verifyPassword(String password) {
        //System.out.println("\nVerifying password with password criteria");
        regExPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(regExPattern);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches()){
            System.out.println("\nPassword has meet the format criteria");
        } else{
            System.out.println("\nPassword does not meet the format criteria");
            helpInfoPassword();
            System.exit(1);
        }

    }

    public void writeToEmailsVerified(String email, String password) throws IOException {
        String encryptedPass = encryptValue(password);
        Path emailStorage= Path.of("/Users/vyasgupta/Desktop/Programs/PersonalProject/src/Emails.txt");
        Files.writeString(emailStorage, email+":"+encryptedPass);

    }
    public void checkEmailExist(String email, String password) throws IOException {
        String encryptPass = encryptValue(password);
        String combined = email + ":" + encryptPass;
        Path emailStorage = Paths.get("/Users/vyasgupta/Desktop/Programs/PersonalProject/src/Emails.txt");
        List<String> lines = Files.readAllLines(emailStorage);
        for (String line :lines ) {
            if(line.contains(combined)) {
                System.out.println("\nEmail has been found in database");
            }
            else {
                System.out.println("\nAccount has not been found in database. Please create your account before enrolling!");
                System.exit(1);
            }

        }
    }

    public String encryptValue(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            //byte[] messageDigest = md.digest(authenticationCode.getBytes());
            byte[] messageDigest = md.digest(value.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            hashCodePass = no.toString(16);

            while (hashCodePass.length() < 32) {
                hashCodePass= "0" + hashCodePass;
            }

        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        return hashCodePass;

    }
    public void helpInfoEmail() {
        System.out.println("Email Requirment criteria below: ");
        System.out.println("Length Restriction: Must be between 1 and 64 characters.");
        System.out.println("Alphanumeric characters (A-Z, a-z, 0-9) ");
        System.out.println("Underscore (_) and hyphen (-) are allowed.");
        System.out.println("Optional dot-separated sections are allowed");
        System.out.println("Required @ Symbol");
        System.out.println("\nDomain Section:");
        System.out.println("Must not start with a hyphen.");
        System.out.println("Contains letters, digits, and hyphens.");
        System.out.println("Allows optional subdomains.");
        System.out.println("\nValid Email Example:");
        System.out.println("user_name-123@sub.example.co");


    }

    public void helpInfoPassword() {
        System.out.println("\nPassword Requirment criteria below:");
        System.out.println("Minimum Length: At least 8 characters.");
        System.out.println("Uppercase Requirement: At least one uppercase letter (A-Z).");
        System.out.println("Lowercase Requirement: At least one lowercase letter (a-z).");
        System.out.println("Digit Requirement: At least one numeric character (0-9).");
        System.out.println("Special Character Requirement: At least one special character from the set @$!%*?&.");
        System.out.println("\nValid Password Example");
        System.out.println("Strong@Pass123");

    }

}
