mvn clean package

mkdir -p plugins
cp demo-plugins/plugin-java-net/target/plugin-java-net-1.0.0-SNAPSHOT.jar plugins/
cp demo-plugins/plugin-okhttp/target/plugin-okhttp-1.0.0-SNAPSHOT.jar plugins/

java -cp "demo-app/target/demo-app-1.0.0-SNAPSHOT.jar:demo-api/target/demo-api-1.0.0-SNAPSHOT.jar:$HOME/.m2/repository/org/pf4j/pf4j/3.10.0/pf4j-3.10.0.jar:$HOME/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar" com.example.app.Main