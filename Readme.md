This is an [ConnID ](https://connid.atlassian.net)  connector to support 
box.com.


1. Turn on Enterprise in Application Access section in Developer Console for the app
1. Turn on Generate User Access Tokens in Advanced Features section in Developer Console for the app
1. Enable JWT https://developer.box.com/docs/authentication-with-jwt
1. Generate a keypair and download the file, 
```
{
  "boxAppSettings": {
    "clientID": "thisisaclientidthatwouldbepresentinthisfileifyoudiditright",
    "clientSecret": "thisisaclientsecretthatwouldbepresentinthisfileifyoudiditright",
    "appAuth": {
      "publicKeyID": "yourpublickeyid",
      "privateKey": "-----BEGIN ENCRYPTED PRIVATE KEY-----\nTHISISNTAREALPRIVATEKEYBUTIFITWEREITWOULDBEAWESOME\n-----END ENCRYPTED PRIVATE KEY-----\n",
      "passphrase": "haveyouthepassphras"
    }
  },
  "enterpriseID": "88888888"
}
```