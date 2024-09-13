
`sdk use java 11.0.24-tem`
`sudo apt-get install libxrender1`
`sudo apt-get install libxtst6`
`sudo apt-get install libxi6`


`mvn -B -V -U -C -Pstaging,oss-release,test-lrg,mysql verify -Dgpg.skip=true -Dwarn.limit=15 -Dcomp.xlint=-Xlint:none -Ddb.driver=com.mysql.cj.jdbc.Driver -Ddb.url=jdbc:mysql://localhost:3306/ecltests -Ddb.user=igor -Ddb.pwd=start`