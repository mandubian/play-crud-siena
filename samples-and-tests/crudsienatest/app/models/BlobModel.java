package models;

import siena.Column;
import siena.Filter;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;


/**
 * @author pascal
 *
 */
public class BlobModel extends Model {
    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    public byte[] image;
}