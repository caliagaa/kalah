//Init script to create database, and required collections

db = db.getSiblingDB('kalah')
db.createCollection("games"); 
db.createCollection("sequence"); 
db.createCollection("gameStatus"); 
db.createCollection("playerTurns");


//Initialize game sequence in zero
db.sequence.save(
{
    name: "game",
    seq: 0
});
