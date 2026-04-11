# Smart4AviationTask

### Solver z wykorzystaniem drzew Fenwicka

Aby uruchomić program, należy wykonać komendę:
`./mvnw clean package`
aby zbudować projekt, a następnie:

`java -jar target/Smart4Aviation-1.0-SNAPSHOT.jar`

Główna logika projektu została stworzona za pomocą typów prymitywnych (ze względu na wysokie ograniczenia podawanych danych)

Dodatkowo klasy `TestRunner` i `TestGenerator` mogą automatycznie tworzyć testy, oraz je sprawdzać.
`TestGenerator` wykorzystuje podejście bruteforce, więc nie polecam dawać parametrów większych niż
$n=10^6$ i $q = 10^6$.