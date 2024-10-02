package inventory.repository.mock;

import inventory.model.InhousePart;
import inventory.model.Part;
import inventory.model.Product;
import inventory.repository.InventoryRepository;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class InventoryRepositoryTestWithMock {

    private InventoryRepository inventoryRepository;

    @Before
    public void setUp() {
        inventoryRepository = mock(InventoryRepository.class);
    }

    @Test
    public void testAddProduct() {
        // Mock pentru Product
        Product product = mock(Product.class);

        // Mock pentru lista de piese asociate produsului
        ObservableList<Part> addParts = mock(ObservableList.class);

        // Setarea listei de piese asociate pe obiectul mock Product
        when(product.getAssociatedParts()).thenReturn(addParts);

        // Apelul metodei addProduct
        inventoryRepository.addProduct(product);

        // Verificarea apelului metodei addProduct cu obiectul Product și lista de piese asociate ca argumente
        verify(inventoryRepository, times(1)).addProduct(eq(product));
    }

    @Test
    public void testUpdateInhousePart() {
        // Mock pentru Part
        Part part = mock(InhousePart.class);

        // Apelul metodei updatePart cu un index și un obiect Part
        inventoryRepository.updatePart(0, part);

        // Verificarea apelului metodei updatePart cu index și obiect Part
        verify(inventoryRepository, times(1)).updatePart(eq(0), any(Part.class));
    }
}
