package models;

import siena.Generator;
import siena.Id;
import siena.Model;

public class Fourword extends Model {
	@Id(Generator.AUTO_INCREMENT)
	public Long id;
	
	public String test;

	public static Fourword getFourword(Long id2) {
		return Model.all(Fourword.class).getByKey(id2);
	}
}
