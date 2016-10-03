## OAuth for the Android Register SDK  

You have downloaded the Register Android SDK sample-bikeshop app and encountered a Not Authorized error. Why?

In order to use a Square merchant's account to process a transaction, your application needs to be authorized by the merchant. Square uses OAuth to manage these permissions. To be authorized, your app must go through the OAuth flow in order to obtain an access token for the merchant's account. Although the Register Android SDK does not require that you provide this token, it checks that one has been issued before completing a transaction. 

### What is OAuth? 

An explanation of OAuth and a description of the OAuth flow as it applies to Square APIs can be found [here](https://docs.connect.squareup.com/api/oauth/).

### How do I complete the OAuth flow from my Android application?

Instructions for completing the OAuth flow for the Register Android SDK can be found on the [Register API Android](http://docs.connect.squareup.com/articles/register-api-android/) page in the docs under the section "Letting other merchants use your app". 

### What about the Bikeshop App?

Your merchant account has now authorized the bikeshop app.

