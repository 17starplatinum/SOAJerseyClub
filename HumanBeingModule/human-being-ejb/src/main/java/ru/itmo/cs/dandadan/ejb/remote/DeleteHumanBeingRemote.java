package ru.itmo.cs.dandadan.ejb.remote;

import jakarta.ejb.Remote;
import ru.itmo.cs.dandadan.util.Result;

@Remote
public interface DeleteHumanBeingRemote {
    Result<Void> deleteHumanBeing(Long id);
}
