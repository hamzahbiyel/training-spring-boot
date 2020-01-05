package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerTest {

    @Mock
    ProductDao productDao;

    @InjectMocks
    ProductController productController;

    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void listeProduits() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1, "testNom", 123, 120));

        Mockito.when(productDao.findAll()).thenReturn(productList);
        MappingJacksonValue result = productController.listeProduits();
        Assert.assertEquals(productList, result.getValue());
    }

    @Test
    void trierProduitsParOrdreAlphabetique() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1, "A testNom", 123, 120));
        productList.add(new Product(2, "B testNom", 124, 120));

        Mockito.when(productDao.trierProduct()).thenReturn(productList);
        assertEquals(productList, productController.trierProduitsParOrdreAlphabetique());
        Mockito.verify(productDao, Mockito.times(1)).trierProduct();

    }

    @Test
    void ajouterProduit() {
        List<Product> productList = new ArrayList<>();
        Product produit = new Product(1, "testNom", 0, 120);

        ProduitGratuitException thrown = assertThrows(ProduitGratuitException.class , () -> productController.ajouterProduit(produit));
        assertEquals("prix de vente ne peut pas etre egal a 0",thrown.getMessage());
    }

    @Test
    void updateProduit() {

        Product product = new Product(1, "testNom", 123, 120);

        Mockito.when(productDao.save(product)).thenReturn(null);
        productController.updateProduit(product);
        Mockito.verify(productDao, Mockito.times(1)).save(product);

    }

    @Test
    void calculerMargeProduit() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1, "testNom", 123, 120));

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat", "marge");

        Mockito.when(productDao.findAll()).thenReturn(productList);
        MappingJacksonValue result = productController.calculerMargeProduit();
        assertNotEquals(null , result.getFilters().findPropertyFilter("monFiltreDynamique",monFiltre));
        assertEquals(productList, result.getValue());

    }

    @Test
    void afficherProduit() {
        Mockito.when(productDao.findById(1)).thenReturn(null);
        ProduitIntrouvableException thrown = assertThrows(ProduitIntrouvableException.class , () -> productController.afficherUnProduit(1));
        assertEquals("Le produit avec l'id 1 est INTROUVABLE. Écran Bleu si je pouvais.",thrown.getMessage());

    }
}