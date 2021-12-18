Feature: Secure Password
   Rule: Password must be at least 6 characters long with at least one number and at least one special character ()
  Scenario: Registration:Incorrect case:Empty Fields
    Given New unregistered user at  registration page
    When User click on JoinNow Button
    Then Error Message "This field is required" occurs

  Scenario: Registration:Incorrect case:Incorrect Password
       Given New unregistered user at  registration page
       When User input 123456 in a Choose Password
       When User input 123457 in a Re-type Password
       Then Error Message "Both fields must match" occurs

# Critical Case:  Max size of password is not specified
# user can input CorrectVeryLongPassword ~ 10MB or more
 Scenario: Registration:Incorrect case:Incorrect Password: CorrectVeryLongPassword
   Given New unregistered user at  registration page
       When User input CorrectVeryLongPassword in a Choose Password
       When User input CorrectVeryLongPassword in a Re-type Password
       Then Error Message "Password too long" occurs

  Scenario Outline: Registration:Incorrect case:Incorrect Password
    Given New unregistered user at  registration page
    When User input incorrect <password1> in a Choose Password
    When User input incorrect <password1> in a Re-type Password
    Then <error> Message occurs
    Examples:
      | password1            | error                                           |
      | '                    | Minimum 6 characters required                   |
      | 12345                | Minimum 6 characters required                   |
      | 123456               | At least one special symbol required            |
      | ______               | At least one number  symbol required            |
      | "      "             | At least one number and special symbol required |
      | aaaaaa               | At least one number and special symbol required |
      | {post_max_size 20MB} | Password too long                               |





     Scenario Outline: Registration:Correct case:Correct Password
       Given New unregistered user at  registration page
       When User input correct <password1> in a Choose Password
       When User input correct <password1> in a Re-type Password
       Then No Error Message occurs
       Examples:
         | password1                           |
         | 01234567890_                        |
         | !"#$%&'()*+,-./:;<=>?@[]^_`{ \| }~0 |
         | \0\n\r\t                            |
         | \r                                  |
         | \t                                  |
         | aaaaaa                              |
