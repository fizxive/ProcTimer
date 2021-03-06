fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
## Android
### android test
```
fastlane android test
```
Runs all the tests
### android lint
```
fastlane android lint
```
Runs lint
### android ktlint
```
fastlane android ktlint
```
Runs ktlint
### android all_checks
```
fastlane android all_checks
```
Runs all checks
### android deploygate_debug
```
fastlane android deploygate_debug
```
Submit a new Debug Build to DeployGate
### android deploy
```
fastlane android deploy
```
Deploy a new version to the Google Play, Alpha track (.aab file only)
### android deploy_artifacts
```
fastlane android deploy_artifacts
```
Deploy artifacts to Google Play (Overwrites old artifacts, be careful)
### android deploy_all
```
fastlane android deploy_all
```
Deploy everything (Overwrites old artifacts, be careful)

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
