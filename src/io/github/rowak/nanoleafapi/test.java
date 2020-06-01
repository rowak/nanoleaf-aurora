package io.github.rowak.nanoleafapi;

import java.util.List;
import java.util.Set;

import io.github.rowak.nanoleafapi.tools.Setup;
import net.straylightlabs.hola.dns.Domain;
import net.straylightlabs.hola.sd.Instance;
import net.straylightlabs.hola.sd.Query;
import net.straylightlabs.hola.sd.Service;

public class test
{
	public static void main(String[] args) throws Exception
	{
//		Service service = Service.fromName("_nanoleafapi._tcp");
//        Query query = Query.createFor(service, Domain.LOCAL);
//        Set<Instance> instances = query.runOnce();
//        instances.stream().forEach(System.out::println);
		List<AuroraMetadata> auroras = Setup.findAuroras();
		for (AuroraMetadata metadata : auroras)
        {
            System.out.println(metadata.getDeviceName() + "  " + metadata.getHostName() + "  " + metadata.getPort() + "  " + metadata.getDeviceId());
        }
	}
}
