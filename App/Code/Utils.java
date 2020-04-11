import java.util.Random;

public class Utils
{
    private static final String[] Locations = new String[]
    {
            "Shipped",
            "In Transit",
            "Out for Delivery",
            "Airdrie, AB",
            "Beaumont, AB",
            "Brooks, AB",
            "Edmonton, AB",
            "Lacombe, AB",
            "Lethbridge, AB",
            "Red Deer, AB",
            "St. Albert, AB",
            "Leduc, AB",
            "Camrose, AB",
            "Abbotsford, BC",
            "Armstrong, BC",
            "Cranbrook, BC",
            "Enderby, BC",
            "Femie, BC",
            "Fort St. John, BC",
            "Fernie, BC",
            "Grand Forks, BC",
            "Colwood, BC",
            "Courtenay, BC",
            "Bathurst, NB",
            "Campbellton, NB",
            "Dieppe, NB",
            "Edmundston, NB",
            "Fredericton, NB",
            "Miramichi, NB",
            "Moncton, NB",
            "Saint John, NB",
            "Ottawa, ON",
            "Toronto, ON",
            "Brockville, ON",
            "Cornwall, ON",
            "Guelph, ON",
            "Belleville, ON",
            "Pembroke, ON",
            "Kingston, ON",
            "Pickering, ON",
            "Owen Sound, ON"
    };

    public static String GenerateLocation()
    {
        Random random = new Random();
        int index     = random.nextInt(Locations.length);

        return Locations[index];
    }
}
