##### Base functionality :
* While designing the service and DAO, I kept in mind the possibility of switching from an in-memory solution to a database. That's why I used interfaces that can easily support a DB-based DAO implementation without any changes.
* Since task condition provided with only one option of representation order for results, I decided to use some default order which is defined during DAO initialization

##### Few words about Subscription functionality :
* Added a subscription mechanism to `WorldCupScoreboardService` to ensure each client receives real-time updates of the full scoreboard.
* This is especially important when the service is accessed concurrently by multiple clients — each responsible for updates related to a specific team — while needing to display the full, up-to-date scoreboard across all clients.
* The parameters map inside `ScoreboardStateUpdateEvent` is used solely to allow subscribers to identify and ignore updates triggered by their own direct calls to `WorldCupScoreboardService`.