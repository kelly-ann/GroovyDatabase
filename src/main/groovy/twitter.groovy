import groovy.sql.GroovyRowResult
import groovy.sql.Sql

String dbUrl = "jdbc:derby://localhost:1527/twitter;create=true;"
String username = 'APP'
String password = 'APP'
String derbyDriver = 'org.apache.derby.jdbc.ClientDriver'

def sql = Sql.newInstance(dbUrl, username, password, derbyDriver)

// create schema
//sql.execute('DROP TABLE IF EXISTS users') // not legal in Derby SQL syntax
sql.execute('DROP TABLE users')
sql.execute '''
CREATE TABLE users (
    id INT NOT NULL,
    username VARCHAR(45) NOT NULL,
    bio VARCHAR(45),
    PRIMARY KEY (id)
)
'''

// create some data
sql.execute '''
INSERT INTO users (id, username, bio) VALUES (1, 'K-A', 'Software. Learning. Positivity. Candlesticks.')
'''

def twitterUser = [id:2, username:"foo", bio:"bar"]

//need triple-DOUBLE-quotes for string interpolation ${}
sql.execute """
INSERT INTO users (id, username, bio)
VALUES (${twitterUser.id}, ${twitterUser.username}, ${twitterUser.bio})
"""

//query table
List<GroovyRowResult> rows = sql.rows("select * from users")
rows.eachWithIndex { row, index ->
    println "Table Row ${index}:\t" + row
}

println ''
sql.eachRow("select * from users") { row ->
    println "Tweet: @${row.username}"
}

// write data to a file
//def writer = new FileWriter()
def file = new File("K-As_Generated_File.csv")
file.write("id|username|bio\n")
rows.each { row ->
    file.append("${row.id}|${row.username}|${row.bio}\n")
}

// manually closing connection
sql.close()

println "\nCompleted!"

