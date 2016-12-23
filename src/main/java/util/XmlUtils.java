package util;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.commons.io.FileUtils;
import player.EvolutionPlayer;
import player.Gene;

/**
 * @author Kolisek
 */
public class XmlUtils
{

	private static String serializePlayer(@Nonnull final EvolutionPlayer player)
	{
		XStream xstream = new XStream(new StaxDriver());
		xstream.omitField(EvolutionPlayer.class, "isEmpty");
		xstream.omitField(EvolutionPlayer.class, "evolutionUtils");
		xstream.omitField(Gene.class, "evolutionUtils");
		StringWriter stringWriter = new StringWriter();
		xstream.marshal(player, new PrettyPrintWriter(stringWriter));
		xstream.alias("genes", Gene.class);
		return stringWriter.toString();
	}

	public static void savePlayer(@Nonnull final EvolutionPlayer player)
	{
		String routePath = System.getProperty("user.dir");
		File file = new File(routePath + File.separator + "data" + File.separator + player.getName() + ".xml");
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
