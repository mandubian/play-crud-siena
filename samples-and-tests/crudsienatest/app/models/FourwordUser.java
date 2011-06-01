package models;

import java.util.Date;
import java.util.List;

import play.Logger;

import siena.Generator;
import siena.Id;
import siena.Model;

public class FourwordUser extends Model {
	@Id(Generator.AUTO_INCREMENT)
	public Long id;
	public Date birthdate;

	public Fourword fourword; // this is the object reference
	public String uid;

	public static Fourword getFourwords(String uid) {
		if (uid == null)
			return null;

		List<FourwordUser> list = Model.all(FourwordUser.class)
				.filter("uid", uid).fetch();

		if (list.size() == 0)
			return null;
		else {
			Long fuid = list.get(0).id;
			FourwordUser fu = FourwordUser.getFourwordUser(fuid);
			Logger.debug("from uid %s , found FourwordUser, fuid = %s ", uid,
					fuid);
			Logger.debug(
					"fu = %s , fu.fourword = %s , fu.birthday=%s , fu.uid = %s",
					fu, fu.fourword, fu.birthdate, fu.uid);
			Logger.debug("fu.fourword.id = %s", fu.fourword.id);

			return Fourword.getFourword(fu.fourword.id);
		}
	}

	public static FourwordUser getFourwordUser(Long id) {
		return FourwordUser.all(FourwordUser.class).filter("id", id).fetch()
				.get(0);
	}
}
