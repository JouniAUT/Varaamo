package fi.varaamo.seed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import fi.varaamo.rooms.Room;
import fi.varaamo.rooms.RoomRepository;

@Component
public class RoomSeeder implements CommandLineRunner {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roomRepository.count() == 0) {
            Room room1 = new Room("Pisara", 3);
            Room room2 = new Room("Puro", 5);
            Room room3 = new Room("Lampi", 10);
            Room room4 = new Room("Virta", 20);
            Room room5  = new Room("Tyrsky", 50);
            Room room6  = new Room("Majakka", 100);

            roomRepository.save(room1);
            roomRepository.save(room2);
            roomRepository.save(room3);
            roomRepository.save(room4);
            roomRepository.save(room5);
            roomRepository.save(room6);
        }
    }
}