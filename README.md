# android-kubernetes-blockchain

![](docs/architecture.png)
## Flow

1. The REST API is how the mobile app will interact with the blockchain network. The API will acknowledge the request and the mobile app will receive a unique key (random numbers and letters) which will be used to get the blockchain’s response later.
2. The API just stores the request in a queue in RabbitMQ. The queue has 2 channels, which are for the user (Fitcoin org) and the seller (Shop org). The requests can either be:
    * User enrollment
    * Query data from the blockchain network (number of kubecoins of a user, products that are for sale, contracts, etc…)
    * Invoke or perform a transaction (send steps to receive kubecoins, claim a product, complete a transaction, etc…)
3. The execution workers use the Hyperledger Fabric Node.js SDK to perform above requests. They are listening to the requests from RabbitMQ.
4. The workers send the requests to the Blockchain network and are then processed. The blockchain network uses NFS to persist the ledger and state database.
5. The workers receive the response and then persists it in the redis database with the unique key from (# 1).
6. The mobile app will continue to wait for the blockchain’s response results that should be in the redis database. This is where the unique key is used. The mobile app will query the redis database with the unique key.
7. The Registration microservice is used to create a user or update the user’s steps. When the blockchain network enrolls a user, this service uses the user id assigned by the blockchain network.
8. When a user is registered, it calls a Cloud Function to generate a random name and avatar for the user.
9. The data from the microservices are persisted in a MongoDB. This is where the user’s data (steps, name and avatar) and mobile assets (booklet/articles in the first view of mobile app) are persisted.
10. The mobile assets microservice is used to query the MongoDB to get dynamic data for the mobile app. The booklet in the first view uses the database for its content.
11. The leaderboard microservice is used to get the standings of the users.


## Summary

The first time the user opens the app, he gets anonymously assigned a unique user ID in the Blockchain network. They also get assigned a random avatar and name which will be stored in MongoDB. This data will be used for the Leaderboard. The 3 microservices (Leaderboard, Mobile Assets, Registration) outside of the blockchain network are Node.js web apps to get data from the MonoDB. As users walk around, their steps will be sent to the blockchain network and they will be rewarded with "Kubecoins". These coins can be used in the blockchain network to trade assets. The assets we have are some swags(stickers, bandanas, etc.) for the KubeCon conference. Users can see and claim them using the app. Once they claim a product, they'll get a Contract ID. They'll show the Contract ID to the seller (us) and we will complete and verify the transaction in our dashboard and give them the swag. The users can also check how they are doing with their steps compared to other people in the Standings view in the mobile app or in the Kubecoin dashboard.

## Included Components

* [IBM Cloud Container Service](https://console.bluemix.net/docs/containers/container_index.html): IBM Bluemix Container Service manages highly available apps inside Docker containers and Kubernetes clusters on the IBM Cloud.
* [Hyperledger Fabric v1.0](https://www.hyperledger.org/projects/fabric): An implementation of blockchain technology that is intended as a foundation for developing blockchain applications or solutions for business.
* [Compose for MongoDB](https://www.ibm.com/cloud/compose/mongodb): MongoDB with its powerful indexing and querying, aggregation and wide driver support, has become the go-to JSON data store for many startups and enterprises.
* [Compose for RabbitMQ](https://www.ibm.com/cloud/compose/rabbitmq): RabbitMQ is a messaging broker that asynchronously communicates between your applications and databases, allowing you to keep seperation between your data and app layers.
* [Compose for Redis](https://www.ibm.com/cloud/compose/redis): An open-source, in-memory data structure store, used as a database, cache and message broker.
* [Cloud Functions](https://www.ibm.com/cloud/functions): Execute code on demand in a highly scalable, serverless environment.

## Featured Technologies

* [Blockchain](https://www.ibm.com/blockchain): Distributed database maintaining a continuously growing list of secured records or blocks.
* [Serverless](https://www.ibm.com/cloud/functions): An event-action platform that allows you to execute code in response to an event.
* [Databases](https://en.wikipedia.org/wiki/IBM_Information_Management_System#.22Full_Function.22_databases): Repository for storing and managing collections of data.

# Steps

### Clone the repo

### Create IBM Cloud services

### Configure the Blockchain Network

### Deploy the Blockchain Network

### Configure the Android app

### Test the Android app
