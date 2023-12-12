package lk.javainstitute.techstoreadmin.listener;

import lk.javainstitute.techstoreadmin.model.Product;

public interface ProductSelectListener {
    void editProduct(Product product);
    void changeStatus(Product product);
}
