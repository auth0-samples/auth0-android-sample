# On Windows, replace ./gradlew with gradlew.bat
./gradlew configureAuth0 -Pdomain=%AUTH0_DOMAIN% -PclientId=%AUTH0_CLIENT_ID% -Pscheme=%AUTH0_SCHEME% -PapplicationId=%APPLICATION_ID%
