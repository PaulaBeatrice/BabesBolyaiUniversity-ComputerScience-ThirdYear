package inventory.service;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import inventory.model.InhousePart;
import inventory.model.OutsourcedPart;
import inventory.model.Part;
import inventory.model.Product;
import inventory.repository.InventoryRepository;
import inventory.service.InventoryService;

public class InventoryServiceTest {

    private InventoryService inventoryService;
    private InventoryRepository inventoryRepository;

    @Before
    public void setUp() {
        inventoryRepository = new InventoryRepository();
        inventoryService = new InventoryService(inventoryRepository);
    }

    @Test
    public void testAddInhousePart() {
        // Test adding an in-house part
        ObservableList<Part> parts = inventoryService.getAllParts();
        int old_size = parts.size();
        inventoryService.addInhousePart("Test Inhouse Part", 10.0, 5, 1, 10, 100);

        parts = inventoryService.getAllParts();
        assertEquals(old_size+1, parts.size());
    }

    @Test
    public void testAddOutsourcePart() {
        // Test adding an outsourced part
        ObservableList<Part> parts = inventoryService.getAllParts();
        int old_size = parts.size();
        inventoryService.addOutsourcePart("Test Outsourced Part", 15.0, 8, 2, 12, "Supplier XYZ");
        parts = inventoryService.getAllParts();
        assertEquals(old_size+1, parts.size());
    }

    @Test
    public void testAddProduct() {
        // Test adding a product
        ObservableList<Part> parts = FXCollections.observableArrayList();
        ObservableList<Product> products = inventoryService.getAllProducts();
        int old_size = products.size();
        parts.add(new InhousePart(1, "Part 1", 10, 1, 20, 50, 123));
        inventoryService.addProduct("Test Product", 50.0, 20, 5, 30, parts);
        products = inventoryService.getAllProducts();
        assertEquals(old_size + 1, products.size());
    }

    @Test
    public void testUpdateInhousePart() {
        // Test updating an in-house part
        inventoryService.addInhousePart("Test Inhouse Part", 10.0, 5, 1, 10, 100);
        ObservableList<Part> parts = inventoryService.getAllParts();
        InhousePart partToUpdate = (InhousePart) parts.get(parts.size()-1); // Se asigură că se folosește tipul corect
        inventoryService.updateInhousePart(parts.size()-1, partToUpdate.getPartId(), "Updated Part", 15.0, 8, 2, 12, 200);
        assertEquals("Updated Part", parts.get(parts.size()-1).getName());
    }


    @Test
    public void testUpdateOutsourcePart() {
        // Test updating an outsourced part
        inventoryService.addOutsourcePart("Test Outsourced Part", 15.0, 8, 2, 12, "Supplier XYZ");
        ObservableList<Part> parts = inventoryService.getAllParts();
        OutsourcedPart partToUpdate = (OutsourcedPart) parts.get(parts.size()-1); // Se asigură că se folosește tipul corect
        inventoryService.updateOutsourcedPart(parts.size()-1, partToUpdate.getPartId(), "Updated Part", 20.0, 10, 3, 15, "New Supplier");
        assertEquals("Updated Part", parts.get(parts.size()-1).getName());
    }


    @Test
    public void testUpdateProduct() {
        // Test updating a product
        ObservableList<Part> parts = FXCollections.observableArrayList();
        parts.add(new InhousePart(2, "Part 2", 10, 1, 20, 50, 123));
        inventoryService.addProduct("Test Product", 50.0, 20, 5, 30, parts);
        ObservableList<Product> products = inventoryService.getAllProducts();
        Product productToUpdate = products.get(0);
        productToUpdate.setName("Updated Product");
        inventoryService.updateProduct(0, productToUpdate.getProductId(), "Updated Product", 60.0, 25, 8, 35, parts);
        assertEquals("Updated Product", products.get(0).getName());
    }

    @Test
    public void testDeletePart() {
        // Test deleting a part
        ObservableList<Part> parts = inventoryService.getAllParts();
        inventoryService.addInhousePart("Test Inhouse Part", 10.0, 5, 1, 10, 100);
        parts = inventoryService.getAllParts();
        int old_size = parts.size();
        inventoryService.deletePart(parts.get(0));
        assertEquals(parts.size(), old_size-1);
    }

    @Test
    public void testDeleteProduct() {
        // Test deleting a product
        ObservableList<Part> parts = FXCollections.observableArrayList();
        parts.add(new InhousePart(3, "Part 3", 10, 1, 20, 50, 123));
        inventoryService.addProduct("Test Product", 50.0, 20, 5, 30, parts);
        ObservableList<Product> products = inventoryService.getAllProducts();
        int old_size = products.size();
        inventoryService.deleteProduct(products.get(0));
        assertEquals(products.size(), old_size-1);
    }
}
