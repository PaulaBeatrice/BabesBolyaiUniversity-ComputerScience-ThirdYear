package inventory.service.mock;

import inventory.model.Part;
import inventory.model.Product;
import inventory.repository.InventoryRepository;
import inventory.service.InventoryService;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class InventoryTestStep2Test {
    private InventoryRepository inventoryRepository;
    private InventoryService inventoryService;

    @Before
    public void setUp() {
        // Inițializăm un InventoryRepository real
        inventoryRepository = new InventoryRepository();
        inventoryService = new InventoryService(inventoryRepository);
    }

    @Test
    public void testAddProduct() {
        Product product = mock(Product.class);

        // Mock pentru lista de piese asociate produsului
        ObservableList<Part> addParts = mock(ObservableList.class);

        // Setarea listei de piese asociate pe obiectul mock Product
        when(product.getAssociatedParts()).thenReturn(addParts);

        int old_size_r = inventoryRepository.getAllProducts().size();
        int old_size_s = inventoryService.getAllProducts().size();

        // Apelul metodei addProduct
        inventoryRepository.addProduct(product);

        assertEquals(inventoryRepository.getAllProducts().size(), old_size_r + 1);
        assertEquals(inventoryService.getAllProducts().size(), old_size_s + 1);
    }

    @Test
    public void deleteProduct() {
        Product product = mock(Product.class);

        ObservableList<Part> addParts = mock(ObservableList.class);

        // Setarea listei de piese asociate pe obiectul mock Product
        when(product.getAssociatedParts()).thenReturn(addParts);

        int old_size_r = inventoryRepository.getAllProducts().size();
        int old_size_s = inventoryService.getAllProducts().size();

        // Apelul metodei addProduct
        inventoryRepository.addProduct(product);

        assertEquals(inventoryRepository.getAllProducts().size(), old_size_r + 1);
        assertEquals(inventoryService.getAllProducts().size(), old_size_s + 1);

        inventoryRepository.deleteProduct(product);

        assertEquals(inventoryRepository.getAllProducts().size(), old_size_r);
        assertEquals(inventoryService.getAllProducts().size(), old_size_s);
    }

}
