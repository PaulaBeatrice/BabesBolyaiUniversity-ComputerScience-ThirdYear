package inventory.repository;

import inventory.model.InhousePart;
import inventory.model.Inventory;
import inventory.model.Part;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class InventoryRepositoryTest {
    private InventoryRepository repo;
    private Inventory inventory;
    private Part part;
    private double price = -23;

    @BeforeEach
    void setUp() {
        try{
            repo = new InventoryRepository();
            System.out.println("Repository initialized successfully");
        }catch (Exception e) {
            System.err.println("Failed to initialize repository: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() { }

    // ECP TESTS:
    @RepeatedTest(5)
    @DisplayName("Invalid Price")
    void addPart_InvalidPrice_ECP() {
        part = new InhousePart(1, "P1", price, 10, 1, 15, 10);
        assertThrows(Exception.class, () -> repo.addPart(part));
    }

    @Test
    @DisplayName("Invalid Minimum 1")
    void addPart_InvalidMinimum1_ECP() {
        part = new InhousePart(1,"P2", 32, 13, 3, 2, 10);
        assertThrows(Exception.class, () -> repo.addPart(part));
    }

    @Test
    @DisplayName("Invalid Minimum 2")
    void addPart_InvalidMinimum2_ECP() {
        part = new InhousePart(1,"P2",32,13,30,31,10);
        assertThrows(Exception.class, () -> repo.addPart(part));
    }

    @ParameterizedTest
    @Test
    @ValueSource(strings = {"5", "1", "3"})
    @DisplayName("Invalid Maximum")
    void addPart_InvalidMaximum_ECP(String str) {
        part = new InhousePart(1,"P3",32,13,1, Integer.parseInt(str),10);
        assertThrows(Exception.class, () -> repo.addPart(part));
    }

    @Test
    @DisplayName("Invalid Stock")
    void addPart_InvalidStock_ECP() {
        part = new InhousePart(1,"P4",32,-100,1,5,10);
        assertThrows(Exception.class, () -> repo.addPart(part));
    }

    // BVA TESTS:

    @Test
    @DisplayName("Invalid Minimum")
    void addPart_InvalidMinimum_BVA() {
        part = new InhousePart(1,"P1",10.3,1,2,10,10);
        assertThrows(Exception.class, () -> repo.addPart(part));
    }

    @Test
    @DisplayName("Valid Minimum:")
    void addPart_ValidMinimum_BVA() {
        System.out.println("Running addPart_ValidMinimum_BVA test");
        part = new InhousePart(1,"P1",10.3,3,2,10,10);
        try {
            System.out.println("Calling addPart() method");
            repo = new InventoryRepository();
            int size = repo.getAllParts().size();
            repo.addPart(part);
            System.out.println("addPart() method returned successfully");
            System.out.println("Asserting true");
            assert (repo.getAllParts().size()==size+1);
        } catch(Exception e) {
            System.out.println("addPart() method threw an exception: " + e.getMessage());
            assertEquals("", e.getMessage());
        }
    }

    @Test
    @DisplayName("Valid Maximum")
    void addPart_ValidMaximum_BVA() {
        part = new InhousePart(1,"P2",10.3,11,2,10,10);
        try {
            repo.addPart(part);
            assert true;
        } catch(Exception e){
            assertEquals("Inventory level is higher than the maximum value.",e.getMessage());
        }
    }

    @Tag("Invalid_Maximum")
    @Test
    void addPart_InvalidMaximum_BVA() {
        part = new InhousePart(1,"P3",10.3,9,2,7,10);
        assertThrows(Exception.class, () -> repo.addPart(part));
    }


    @Test
    @DisplayName("Search item found")
    void lookUpProduct_TC02() {
        Assertions.assertEquals(inventory.lookupProduct("Clock").getProductId(), 1);
    }

    @Test
    @DisplayName("Search item not found")
    void lookUpProduct_TC03() {
        assertNull(inventory.lookupProduct("TEST"));
    }
}