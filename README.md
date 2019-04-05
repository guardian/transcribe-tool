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
 - Run using sbt: `sbt "run 9050"`. (For quick restart you should run `sbt` and then `run 9050`, so that you can exit
  the application without exiting sbt.)
  
## Compiling Client Side Dependencies

This project requires Node version 6. To manage different versions of node you can use [node version manager](https://github.com/creationix/nvm).

You can compile client side dependencies with `yarn build` or `npm run build`. 
Alternatively to compile client side assets on change run `yarn build-dev` or `npm run build-dev`

