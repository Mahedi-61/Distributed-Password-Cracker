# Distributed-Password-Cracker
#This project is developed for my project submission in ICT-6544: Distributed Systems course in BUET. 

<h2>Introduction</h2>
<p>The goal of this project is to create a distributed system that can run across the entire Internet. 
We know password cracking is an entirely parallel application. 
It consists of a set of operations on small chunks of data. And therefore no data needs to be shared between different password cracking nodes as they crack. 
They just receive a work unit allocation, try all of the passwords in that unit, and tell the server if any of them was a match.</p>

<h2>Procedure</h2>
<p> My password breaking system consists of two programs, the worker client and the server. 
The server creates a password cracking job and is responsible for dividing the job into parts and 
allocating those parts to worker clients. 
The clients and server communicate with each other using the sockets API to send UDP packets. 
Here the Server sends the actual hash password and a range to the client. 
The Client after get connected to the server receive the hash and the range and 
try to generate all possible passwords within the range and hash it to compare it to the actual hash password. 
If any generated password hash matches to the actual given hash then the client successfully crack the password.
</p>

<h2>Summary</h2>
<p>When the server starts running it generate a hash with a combination of a random password and system date. Then it awaits for incoming client request. The client received a range from the server. If it fails to get the password within the range, it sends a RETRY packet to server .When a client successfully gets the password, it prints the password, and also notifies the server about it. But if it does not, it terminates with a failure message. </p>
