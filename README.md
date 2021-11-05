# FX_and_SOCKET
reference - part of project

Client excludes: AutoReload, ChartStage, Client, Client_log, LoginStage, DBConnect, Message
Server excludes: Server, Server_log, DBdriver, Message

Login is through socket and the ChartStage takes data directly from database.
The socket solution is safe.

AutoReload is indipendent thread that keeps an eye on databse changes to pass on updates.
