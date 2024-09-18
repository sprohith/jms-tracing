**To create an artemis server on alpine use the below command:**

docker run -e ANONYMOUS_LOGIN=true --name myart -it -p 61616:61616 -p 8161:8161 apache/activemq-artemis:latest-alpine

**To test the projects - simply run the consumer first followed by the producer which are PSVM files.**
