This txt is the instruction of how to use the whiteboard system
First, you need to create the manager first, and then is client, jar command line is below

Manager: java -Dsun.java2d.metal=false -jar CreateWhiteBoard.jar 127.0.0.1 1025 Howard
Client: java -jar MemberJoin.jar 127.0.0.1 1025 Jack

Below is the source file command line

Manager: java -Dsun.java2d.metal=false -cp "lib/gson-2.13.1.jar:out" Manager.CreateWhiteBoard 127.0.0.1 1025 Howard

Client: java -cp "lib/gson-2.13.1.jar:out" Client.MemberJoinWhiteBoard 127.0.0.1 1025 Jack

