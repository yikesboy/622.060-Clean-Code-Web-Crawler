# 622.060	Clean Code

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=yikesboy_622.060-Clean-Code-Web-Crawler&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=yikesboy_622.060-Clean-Code-Web-Crawler)

Web-Crawler in Java, which provides a compact overview of the given website and linked websites by only listing the
headings and the links.

Run the WebCrawler.

`mvn exec:java -Dexec.mainClass="io.github.yikesboy.WebCrawler" -Dexec.args="<root-domain> <max-depth> <allowed-domains-comma-separeated"`

Run the Tests.

`mvn test`