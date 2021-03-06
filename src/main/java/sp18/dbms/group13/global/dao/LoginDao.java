package sp18.dbms.group13.global.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import sp18.dbms.group13.global.model.FavCookedOrOther;
import sp18.dbms.group13.global.model.LoginForm;
import sp18.dbms.group13.global.model.Rating;
import sp18.dbms.group13.global.model.Recipe;
import sp18.dbms.group13.global.model.UserDetails;
import sp18.dbms.group13.global.model.UserProfile;

@Repository
public class LoginDao {
	
	@Autowired JdbcTemplate jdbcTemplate;

	public boolean createDiet(LoginForm loginForm) {
		
		int result = jdbcTemplate.update("insert into diet (id,name) values ("+loginForm.getDietID()+",'"+loginForm.getOtherList()+"')");
		if(result>0) {
			return true;
			
		}
		else return false;
	}

	public boolean createRegistration(LoginForm loginForm) {
		
		String query1 = " insert into user_details (userid, fname, lname, password, gender, dob) values (?,?,?,?,?,?)";
		int result = jdbcTemplate.update(query1,loginForm.getUserid(),loginForm.getFirstName(),loginForm.getLastName(),loginForm.getStrPassword(),loginForm.getStrGender(),loginForm.getDateOfBirth());
		
        int count = jdbcTemplate.queryForObject("select count(*) from user_profile", Integer.class)+1; 
		
		String query2 = " insert into user_profile (id, userid, dietid, lifestyle, weight, height, isvalid) values (?,?,?,?,?,?,?)";
		int result2 = jdbcTemplate.update(query2, count,loginForm.getUserid(),loginForm.getDietID(),loginForm.getStrLifeStyle(),loginForm.getWeight(),loginForm.getHeight(),1);
		
		
		if(result>0 && result2>0) {
			return true;
		}
		return false;
	}

	public List<UserDetails> checkLogin(LoginForm loginForm) {
		
		String query = " select * from user_details where userid = '"+loginForm.getUserid()+"'";
		List<UserDetails> users  = jdbcTemplate.query(query,
				new BeanPropertyRowMapper(UserDetails.class));
		
		return users;
	}

	public List<UserProfile> getUserDetails(LoginForm loginForm) {
		String query = " select * from user_profile where userid = '"+loginForm.getUserid()+"' and isvalid = 1";
		List<UserProfile> users  = jdbcTemplate.query(query,
				new BeanPropertyRowMapper(UserProfile.class));
		
		return users;
	}

	public List<Recipe> getRecipeDetails(Recipe recipe) {
		
		//String query = "  select * from recipe where id in (select r.id from ingredients i, recipe r, ingredient_recipe_mapping irm where lower(i.name) like '%"+recipe.getSearchString().toLowerCase()+"%' " + 
		//		"and i.id = irm.ingredientid and r.id = irm.recipeid )";
		String[] ingredientList = recipe.getIngr().split(",");
		String[] dietList = recipe.getDiet().split(",");
		
		List<Recipe> queryTester = new ArrayList<Recipe>();
		
		//Query for ingredient list
		String q1 = "";
		if(ingredientList.length>0) {
			String ingredient = " like '%";
			String ingstr = "";
			for(String ing:ingredientList) {
				ingredient+=ing.toLowerCase()+"%' or lower(name) like '%";
				ingstr+=ing;
			}
			ingredient = ingredient.substring(0,ingredient.length()-23);
			
			if(ingstr.trim().length()>0)
			q1 = "Select * from recipe where id in (select distinct(recipeid) from ingredient_recipe_mapping where ingredientid in (select distinct(id) from ingredients where name "+ingredient+" )) union all ";
		}
		
		
		//Query for diet list
		String q2 = "";
		if(dietList.length>0) {
			String diet = " like '%";
			String dietstr = "";
			for(String die:dietList) {
				diet+=die.toLowerCase()+"%' or lower(name) like '%";
				dietstr+=die;
			}
			diet = diet.substring(0,diet.length()-23);
			
			if(dietstr.trim().length()>0)
			q2 = "Select * from recipe where id in (select distinct(recipeid) from recipe_diet_mapping where dietid in ( select distinct(id) from diet where name "+diet+" ))  union all ";
		}
		
		
		//Query for preptime
		String q3 = "";
		int minPrepTime = 0;
		int maxPrepTime = 0;
		
		if(!recipe.getPrepTimeMin().equals(""))
			minPrepTime = Integer.parseInt(recipe.getPrepTimeMin());
		
		if(!recipe.getPrepTimeMax().equals(""))
			maxPrepTime = Integer.parseInt(recipe.getPrepTimeMax());
		
		q3 = "Select * from recipe where id in (select distinct(id) from recipe where preptime between "+minPrepTime+" and "+maxPrepTime+ ")  union all ";
		
		if((minPrepTime+maxPrepTime == 0)) {
			q3 = "";
		}
		
		
		
		//Query for cooktime
		String q4 = "";
		int minCookTime = 0;
		int maxCookTime = 0;
		
		if(!recipe.getCookTimeMin().equals(""))
			minCookTime = Integer.parseInt(recipe.getCookTimeMin());
		
		if(!recipe.getCookTimeMax().equals(""))
			maxCookTime = Integer.parseInt(recipe.getCookTimeMax());
		
		q4 = "Select * from recipe where id in (select distinct(id) from recipe where cooktime between "+minCookTime+" and "+maxCookTime+ ")  union all ";
		
		if(queryTester.size()==0|| (minCookTime+maxCookTime == 0)) {
			q4 = "";
		}
		
		
		
		//Query for totaltime
		String q5 = "";
		int minTotalTime = 0;
		int maxTotalTime = 0;
		
		if(!recipe.getTotalTimeMin().equals(""))
			minTotalTime = Integer.parseInt(recipe.getTotalTimeMin());
		
		if(!recipe.getTotalTimeMax().equals(""))
			maxTotalTime = Integer.parseInt(recipe.getTotalTimeMax());
		
		q5 = "Select * from recipe where id in (select distinct(id) from recipe where (to_number(cooktime)+to_number(preptime)) between "+minTotalTime+" and "+maxTotalTime+ ")  union all ";
		
		if((minTotalTime+maxTotalTime == 0)) {
			q5 = "";
		}
		
		
		
		//Query for calories
		String q6 = "";
		int minCalories = 0;
		int maxCalories = 0;
		
		if(!recipe.getCaloriesMin().equals(""))
			minCalories = Integer.parseInt(recipe.getCaloriesMin());
		
		if(!recipe.getCaloriesMax().equals(""))
			maxCalories = Integer.parseInt(recipe.getCaloriesMax());
		
		q6 = "Select * from recipe where id in (select distinct(recipeid) from nutritional_information where calories between "+minCalories+" and "+maxCalories+ ")  union all ";
		
		if((minCalories+maxCalories == 0)) {
			q6 = "";
		}
		
		//Query for cholesterol
		String q7 = "";
		int minCholesterol = 0;
		int maxCholesterol = 0;
		
		if(!recipe.getCholesterolMin().equals(""))
			minCholesterol = Integer.parseInt(recipe.getCholesterolMin());
		
		if(!recipe.getCholesterolMax().equals(""))
			maxCholesterol = Integer.parseInt(recipe.getCholesterolMax());
		
		q7 = "Select * from recipe where id in (select distinct(recipeid) from nutritional_information where cholestrol between "+minCholesterol+" and "+maxCholesterol+ ")  union all ";
		
		if((minCholesterol+maxCholesterol == 0)) {
			q7 = "";
		}
		
		//Query for sodium
		String q8 = "";
		int minSodium = 0;
		int maxSodium = 0;
		
		if(!recipe.getSodiumMin().equals(""))
			minSodium = Integer.parseInt(recipe.getSodiumMin());
		
		if(!recipe.getSodiumMax().equals(""))
			maxSodium = Integer.parseInt(recipe.getSodiumMax());
		
		q8 = "Select * from recipe where id in (select distinct(recipeid) from nutritional_information where sodium between "+minSodium+" and "+maxSodium+ ")  union all ";
		
		if((minSodium+maxSodium == 0)) {
			q8 = "";
		}
		
		
		//Query for protein
		String q9 = "";
		int minProtein = 0;
		int maxProtein = 0;
		
		if(!recipe.getProteinMin().equals(""))
			minProtein = Integer.parseInt(recipe.getProteinMin());
		
		if(!recipe.getProteinMax().equals(""))
			maxProtein = Integer.parseInt(recipe.getProteinMax());
		
		q9 = "Select * from recipe where id in (select distinct(recipeid) from nutritional_information where protein between "+minProtein+" and "+maxProtein+ ")  union all ";
		
		if((minProtein+maxProtein == 0)) {
			q9 = "";
		}
		

		//Query for carb
		String q10 = "";
		int minCarb = 0;
		int maxCarb = 0;
		
		if(!recipe.getCarbohydrateMin().equals(""))
			minCarb= Integer.parseInt(recipe.getCarbohydrateMin());
		
		if(!recipe.getCarbohydrateMax().equals(""))
			maxCarb = Integer.parseInt(recipe.getCarbohydrateMax());
		
		q10 = "Select * from recipe where id in (select distinct(recipeid) from nutritional_information where carb between "+minCarb+" and "+maxCarb+ ")  union all ";
		
		if((minCarb+maxCarb == 0)) {
			q10 = "";
		}
		
		
		//Query for fat
		String q11 = "";
		int minFat = 0;
		int maxFat = 0;
		
		if(!recipe.getFatMin().equals(""))
			minFat= Integer.parseInt(recipe.getFatMin());
		
		if(!recipe.getFatMax().equals(""))
			maxFat = Integer.parseInt(recipe.getFatMax());
		
		q11 = " Select * from recipe where id in ( select distinct(recipeid) from nutritional_information where fat between "+minFat+" and "+maxFat + ")  union all ";
		
		if((minFat+maxFat == 0)) {
			q11 = "";
		}		
				
		
		String queryFinal = q1+q2+q3+q4+q5+q6+q7+q8+q9+q10+q11;
		
		if(queryFinal.length()>11) {
			queryFinal = queryFinal.substring(0,queryFinal.length()-11);
		}
		//System.out.println(queryFinal);
				
		String queryUnique = "select * from recipe where id in ( select max(id) from ("+queryFinal+") group by name) order by rating, name";
		
		System.out.println(queryUnique);
		List<Recipe> users  = jdbcTemplate.query(queryUnique,
				new BeanPropertyRowMapper(Recipe.class));
		
		return users;
	}

	public List<Recipe> getChefDetails(Recipe recipe) {
		String query = "  select * from recipe where id in (select r.id from recipe r where lower(r.chefname) like '%"+recipe.getSearchString().toLowerCase()+"%' ) " ;
		System.out.println(query);
		List<Recipe> users  = jdbcTemplate.query(query,
				new BeanPropertyRowMapper(Recipe.class));
		
		return users;
	}

	public boolean updateProfileAndRegistration(LoginForm loginForm) {

		String query = " select * from user_profile where userid = '"+loginForm.getUserid()+"' and isvalid = 1";
		List<UserProfile> users  = jdbcTemplate.query(query,
				new BeanPropertyRowMapper(UserProfile.class));
		
		int result = 0;
		if(users.size()>0) {
			String query1 = " update user_profile set isvalid = 0 where userid = '"+loginForm.getUserid()+"'";
			result = jdbcTemplate.update(query1);
		}
		
		
		boolean update = false;
		String query2 = " update user_details set ";
		if(!loginForm.getFirstName().trim().equals("")) {
			query2 += " fname = '"+loginForm.getFirstName()+"'";
			update = true;
		}
		if(!loginForm.getFirstName().trim().equals("")) {
			query2 += " fname = '"+loginForm.getFirstName()+"'";
			update = true;
		}
		int result2 = 0;
		if(update) {
			result2 = jdbcTemplate.update(query2);
		}

        int count = jdbcTemplate.queryForObject("select count(*) from user_profile", Integer.class)+1; 

		String query3 = " insert into user_profile (id, userid, dietid, lifestyle, weight, height, isvalid) values (?,?,?,?,?,?,?)";
		int result3 = jdbcTemplate.update(query3, count,loginForm.getUserid(),(loginForm.getDietID()!=null && !loginForm.getDietID().trim().equals(""))?loginForm.getDietID():users.get(0).getDietid(),
				(loginForm.getStrLifeStyle()!=null && !loginForm.getStrLifeStyle().trim().equals(""))?loginForm.getStrLifeStyle():users.get(0).getLifestyle(),
						!(loginForm.getWeight()==0)?loginForm.getWeight():users.get(0).getWeight(),
								!(loginForm.getHeight()==0)?loginForm.getHeight():users.get(0).getHeight(),1);


		if(result>0 && result2>0 && result3>0) {
			return true;
		}
		
		return false;
	}

	public int calculateSchemaRows(LoginForm loginForm) {
		String query = "select sum(get_rows(table_name)) as totalrows from user_tables";
        int count = jdbcTemplate.queryForObject(query, Integer.class); 
		return count;
	}

	public boolean insertIntoHistory(LoginForm loginForm) {
        int count = jdbcTemplate.queryForObject("select count(*) from history", Integer.class)+1; 
		String query = "insert into history (id, userid, recipeid, DT) values ("+count+",'"+loginForm.getUserid()+"',"+loginForm.getRecipeId()+",sysdate)";
		int result = jdbcTemplate.update(query);
		
		if(result>0)
			return true;
		else
			return false;
	}

	public Recipe getRecipeDetail(String id, String userId) {
		String query = "select t1.*,t2.isFav, t3.isCook, t4.isOther,t4.names from "+
				" (SELECT r.ID, r.NAME, replace(r.DESCRIPTION,'''', '') as description, r.CHEFNAME, r.PREPTIME, r.COOKTIME, replace(nvl(r.INSTRUCTIONS,''),'''', '') as instructions, "+
				" 	nvl(r.CUISINE,'NA') as cuisine, nvl(r.RATING,0) as rating, "
				+ " nvl(r.NUM_OF_REVIEWS,0) as num_of_reviews, nvl(r.SERVINGS,1) as servings, "+ 
				" 	r.RECIPE_LINK, nvl(r.KEYWORDS,'') as keywords, nvl(n.RECIPEID,0)as recipeid, nvl(n.CHOLESTROL,0) as cholestrol, " + 
				"	nvl(n.SODIUM,0) as sodium, nvl(n.PROTEIN,0) as protein, nvl(n.CARB,0) as carb, nvl(n.FAT,0) as fat, nvl(n.CALORIES,0) as calories "+
				" 	FROM recipe r LEFT OUTER JOIN nutritional_information n "+
				" 	on r.ID = n.RECIPEID "+
				" 	where r.id = "+Integer.parseInt(id)+ ") t1, "+
				" 	(select count(*) as isFav from FAVORCOOKED F, recipe r2 where F.\"recipeid\" = r2.id and F.\"is_FAV\"= 1 "+
				" 	and r2.id = "+Integer.parseInt(id)+ " and F.\"is_valid\"=1 "+
				" 	and F.USERID = '"+userId+"') t2, "+
				" 	(select count(*) as isCook from FAVORCOOKED F, recipe r2 where F.\"recipeid\" = r2.id and F.\"is_COOKED\"= 1 "+
				" 	and r2.id = "+Integer.parseInt(id)+ " and F.\"is_valid\"=1 "+
				" 	and F.USERID = '"+userId+"') t3, "+
				" 	(select count(*) as isOther, listagg(F.OTHER_NAME, ',') within group ( order by F.ID ) as names from FAVORCOOKED F, "+
				"   recipe r2 where F.\"recipeid\" = r2.id and F.\"is_OTHER\"= 1 "+
				" 	and r2.id = "+Integer.parseInt(id)+ " and F.\"is_valid\"=1 "+
				" 	and F.USERID = '"+userId+"') t4 "; 
		List<Recipe> users  = jdbcTemplate.query(query, new BeanPropertyRowMapper(Recipe.class));
		
		System.out.println(query);
		return users.size()>0?users.get(0):null;
	}

	public boolean insertIntoFavOrCooked(FavCookedOrOther favForm, String userId) {
		String insertOrDelete = favForm.getFlag();
		int isFav = 0;
		int isCooked = 0;
		int isOther = 0;
		if(favForm.getListName().equals("fav")) {
			isFav = 1;
		}
		else if(favForm.getListName().equals("cook")) {
			isCooked = 1;
		}
		else if(favForm.getListName().equals("other")) {
			isOther = 1;
		}
		
		int result  = 0;
		
		//Case insert, flag = 1
		if(insertOrDelete.equals("1")) {
			String query = " insert into favorcooked (favorcooked.id,favorcooked.userid,favorcooked.\"is_FAV\",favorcooked.\"is_COOKED\",favorcooked.\"is_OTHER\", " + 
					" favorcooked.other_name,favorcooked.\"recipeid\",favorcooked.\"is_valid\") " + 
					" select max(id)+1 ,'"+userId+"',"+isFav+","+isCooked+","+isOther+",'"+favForm.getOtherListNames()+"',"+favForm.getRecipeid()+",1 from favorcooked ";
			
			result = jdbcTemplate.update(query);
		}
		
		if(insertOrDelete.equals("0")) {
			String query = " update favorcooked " + 
					" set FAVORCOOKED.\"is_valid\" = 0 " + 
					" where FAVORCOOKED.\"recipeid\" = "+favForm.getRecipeid()+ 
					" and FAVORCOOKED.USERID = '"+userId+"' ";
			
			result = jdbcTemplate.update(query);
		}
		
				
		if(result>0)
			return true;
		else
			return false;
	}

	public boolean updateRatings(Rating rating) {
		int result = 0;
		String query = "update recipe " + 
				"set rating = (((num_of_reviews*rating)+"+rating.getRating()+")/(num_of_reviews+1)), num_of_reviews = num_of_reviews+1 " + 
				"where id = "+rating.getRating();
		result = jdbcTemplate.update(query);
		if(result>0)
			return true;
		else
			return false;
	}
	
	
	
}
