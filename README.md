# Transcribe Tool

This is an attempt to use the BBC's excellent [Transcript Editor](https://github.com/bbc/react-transcript-editor) at 
The Guardian.

## Running locally

You'll need the [AWS CLI](http://docs.aws.amazon.com/cli/latest/userguide/installing.html) installed, and credentials
for the composer AWS account from [janus](https://janus.gutools.co.uk). You'll also need to follow the
'Install SSL certificates' step in the [dev-nginx readme](https://github.com/guardian/dev-nginx). Then:

 - Fetch config from S3: `./fetch-config.sh`
 - Setup the nginx mapping by following the instructions in the
 [dev-nginx readme](https://github.com/guardian/dev-nginx#install-config-for-an-application).
 - Install Client Side Dependencies with `./setup.sh`
 - Run using sbt: `sbt run`. (For quick restart you should run `sbt` and then `run`, so that you can exit
  the application without exiting sbt.)
  
## Compiling Client Side Dependencies

Client side code is in the client/ subdirectory.
This project requires Node version 8. To manage different versions of node you can use [node version manager](https://github.com/creationix/nvm).

You can compile client side dependencies with `yarn build` or `npm run build`. 
There is no watch script currently. Phil just does `watch yarn build` but apparently that's not very grown up.

