package hexlet.code.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
//@ConfigurationProperties(prefix = "rsa")
@Setter
@Getter
public class RsaKeyProperties {

    @Value("${public-key}")
    private RSAPublicKey publicKey;
    @Value("${private-key}")
    private RSAPrivateKey privateKey;
}
