package ua.fitness;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.fitness.exception.InvalidDataException;
import ua.fitness.model.*;
import ua.fitness.util.TrainerLoader;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FitnessTests {

    @Test
    @DisplayName("1. Should correctly create Trainer record")
    void shouldCreateTrainerCorrectly() {
        Trainer trainer =new Trainer("TR-01", "Max Power", Specialty.GYM);
        
        assertAll("Trainer fields",
            () -> assertEquals("TR-01", trainer.trainerId()),
            () -> assertEquals("Max Power", trainer.name()),
            () -> assertEquals(Specialty.GYM, trainer.specialty())
        );
    }

    @Test
    @DisplayName("2. Should throw InvalidDataException for incorrect column count")
    void shouldThrowExceptionOnMissingColumns() {
        String invalidLine = "TR-01,Ivan"; 
        
        InvalidDataException e =assertThrows(InvalidDataException.class, 
            () -> TrainerLoader.parseTrainerFromLine(invalidLine, 1));
            
        assertTrue(e.getMessage().contains("Expected 3 columns"));
    }

    @Test
    @DisplayName("3. Should throw InvalidDataException for invalid enum value")
    void shouldThrowExceptionOnInvalidEnum() {
        String invalidLine="TR-02,Anna,NOT_A_SPORT"; 
        
        InvalidDataException e =assertThrows(InvalidDataException.class, 
            () -> TrainerLoader.parseTrainerFromLine(invalidLine, 2));
            
        assertTrue(e.getMessage().contains("Invalid data format"));
        assertNotNull(e.getCause()); 
    }

    @Test
    @DisplayName("4. Should throw InvalidDataException for empty name")
    void shouldThrowExceptionOnEmptyName() {
        String invalidLine = "TR-03,,YOGA"; 
        
        InvalidDataException e = assertThrows(InvalidDataException.class, 
            () -> TrainerLoader.parseTrainerFromLine(invalidLine, 3));
            
        assertTrue(e.getMessage().contains("Invalid data format"));
    }

    @Test
    @DisplayName("5. Should throw IllegalArgumentException in setter of mutable class")
    void shouldThrowExceptionInSetter() {
        Trainer dummyTrainer=new Trainer("T-1", "Test", Specialty.GYM);
        Member dummyMember=new Member("M-1", "Test Member", LocalDate.of(2000, 1, 1));
        MembershipPass dummyPass=new MembershipPass(
            "PASS-1", 
            dummyMember, 
            PassType.values()[0], 
            LocalDate.now(), 
            LocalDate.now().plusDays(30), 
            1000.0
        );
        
        WorkoutSession session = new WorkoutSession(dummyTrainer, dummyPass, LocalDate.now().plusDays(5), 60);
        
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, 
            () ->session.setDurationMinutes(500)); 
            
        assertTrue(e.getMessage().contains("durationMinutes"));
    }

    @Test
    @DisplayName("6. Should update mutable class state correctly via setter")
    void shouldUpdateStateViaSetter() {
        Trainer dummyTrainer = new Trainer("T-1", "Test", Specialty.GYM);
        Member dummyMember = new Member("M-1", "Test Member", LocalDate.of(2000, 1, 1));
        MembershipPass dummyPass = new MembershipPass(
            "PASS-1", 
            dummyMember, 
            PassType.values()[0], 
            LocalDate.now(), 
            LocalDate.now().plusDays(30), 
            1000.0
        );
        
        WorkoutSession session = new WorkoutSession(dummyTrainer, dummyPass, LocalDate.now().plusDays(5), 60);
        
        session.setDurationMinutes(90); 
        assertEquals(90, session.getDurationMinutes());
    }
}