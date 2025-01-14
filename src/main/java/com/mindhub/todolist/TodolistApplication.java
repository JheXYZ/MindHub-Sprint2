package com.mindhub.todolist;

import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class TodolistApplication {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(TodolistApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TodolistApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, TaskRepository taskRepository) {
        return args -> {
            log.info("Populating Users...");
            userRepository.saveAll(
                    List.of(
                            new UserEntity("armando@email.com", passwordEncoder.encode("password123"), "ArmandoParedes", UserAuthority.USER),
                            new UserEntity("amelia@email.com", passwordEncoder.encode("longerPassword544"), "amelia_1990", UserAuthority.USER),
                            new UserEntity("josecardamomo@email.com", passwordEncoder.encode("joseKpo777"), "JoseCapo777", UserAuthority.USER),
                            new UserEntity("jhex@email.com", passwordEncoder.encode("admin123"), "JheX", UserAuthority.ADMIN)
                    )
            );
            log.info("Successfully populated Users!");
            log.info("Populating Tasks...");
            taskRepository.saveAll(
                    List.of(
                            new Task("Title of this task", "Description for this task", TaskStatus.PENDING, userRepository.findById(1L).orElse(null)),
                            new Task("Do the dishes", "", TaskStatus.IN_PROGRESS, userRepository.findById(2L).orElse(null)),
                            new Task("Do the laundry", "Do the landry with John's clothes", TaskStatus.PENDING, userRepository.findById(2L).orElse(null)),
                            new Task("Do my bed", "", TaskStatus.COMPLETED, userRepository.findById(2L).orElse(null)),
                            new Task("Go to the gim", "At 17:30", TaskStatus.PENDING, userRepository.findById(3L).orElse(null)),
                            new Task("Send Armando the new documents", "Due to 8/1 until 10:00", TaskStatus.COMPLETED, userRepository.findById(3L).orElse(null)),
                            new Task("Daily meet of MindHub", "On weekdays usually at 9:10", TaskStatus.IN_PROGRESS, userRepository.findById(3L).orElse(null))
                    )
            );
            log.info("Successfully populated Tasks!");
            /*printResults(userRepository, taskRepository);*/
        };
    }

    private void printAllUsers(UserRepository userRepository) {
        userRepository.findAll().forEach(userEntity -> System.out.println(userEntity.toStringWithoutTasks()));
    }

    private void printAllTasks(TaskRepository taskRepository) {
        taskRepository.findAll().forEach(task -> System.out.println(task.toStringWithoutUser()));
    }

    private void printResults(UserRepository userRepository, TaskRepository taskRepository) {
        System.out.println("----");
        printAllUsers(userRepository);
        System.out.println("----");
        printAllTasks(taskRepository);
        System.out.println("----");
    }
}
