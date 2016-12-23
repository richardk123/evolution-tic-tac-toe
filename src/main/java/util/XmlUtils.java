package util;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;
import player.EvolutionPlayer;

/**
 * @author Kolisek
 */
public class XmlUtils
{

	private static String serializePlayer(@Nonnull final EvolutionPlayer player)
	{
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(player);
	}

	public static void savePlayer(@Nonnull final EvolutionPlayer player)
	{
		String routePath = System.getProperty("user.dir");
		File file = new File(routePath + File.separator + player.getName() + ".xml");
		String playerData = serializePlayer(player);

		try
		{
			FileUtils.writeStringToFile(file, playerData, "UTF-8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
