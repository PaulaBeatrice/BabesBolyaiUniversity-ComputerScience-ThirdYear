package inventory.service.mock;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import inventory.model.InhousePart;
import inventory.model.Part;
import inventory.model.Product;
import inventory.repository.InventoryRepository;
import inventory.service.InventoryService;

public class InventoryServiceTestWithMockConstructorTest {
    private InventoryRepository inventoryRepository;
    private InventoryService inventoryService;

    @Before
    public void setUp() {
        inventoryRepository = mock(InventoryRepository.class);
        inventoryService = new InventoryService(inventoryRepository);
    }

    @Test
    public void testAddProduct() {
        // Create mock data
        ObservableList<Part> parts = FXCollections.observableArrayList();
        parts.add(new InhousePart(1, "Test Part", 5.0, 10, 1, 20, 50));

        // Mock repository method call
        doNothing().when(inventoryRepository).addProduct(any(Product.class));

        // Call service method
        inventoryService.addProduct("Test Product", 50.0, 20, 5, 30, parts);

        // Verify that the addProduct method was called with the correct arguments
        verify(inventoryRepository, times(1)).addProduct(any(Product.class));
    }

    @Test
    public void testUpdateInhousePart() {
        // Create mock data
        InhousePart inhousePartToUpdate = new InhousePart(1, "Test Inhouse Part", 10.0, 5, 1, 10, 100);

        // Mock repository method call
        doNothing().when(inventoryRepository).updatePart(anyInt(), any(InhousePart.class));

        // Call service method
        inventoryService.updateInhousePart(0, 1, "Updated Part", 15.0, 8, 2, 12, 200);

        // Verify that the updatePart method was called with the correct arguments
        verify(inventoryRepository, times(1)).updatePart(eq(0), any(InhousePart.class));
    }
}
