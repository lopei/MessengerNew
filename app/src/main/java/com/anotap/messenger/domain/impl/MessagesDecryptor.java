package com.anotap.messenger.domain.impl;

import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.crypt.AesKeyPair;
import com.anotap.messenger.crypt.CryptHelper;
import com.anotap.messenger.crypt.EncryptedMessage;
import com.anotap.messenger.db.interfaces.IStores;
import com.anotap.messenger.domain.IMessagesDecryptor;
import com.anotap.messenger.model.CryptStatus;
import com.anotap.messenger.model.Message;
import com.anotap.messenger.util.Pair;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;

import static com.anotap.messenger.util.Objects.isNull;
import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by admin on 05.09.2017.
 * phoenix
 */
public class MessagesDecryptor implements IMessagesDecryptor {

    private final IStores store;

    public MessagesDecryptor(IStores store) {
        this.store = store;
    }

    @Override
    public SingleTransformer<List<Message>, List<Message>> withMessagesDecryption(int accountId) {
        return single -> single
                .flatMap(messages -> {
                    final List<Pair<Integer, Long>> sessions = new ArrayList<>(0);
                    final List<Pair<Message, EncryptedMessage>> needDecryption = new ArrayList<>(0);

                    for (Message message : messages) {
                        if (message.getCryptStatus() != CryptStatus.ENCRYPTED) {
                            continue;
                        }

                        try {
                            EncryptedMessage em = CryptHelper.parseEncryptedMessage(message.getBody());

                            if (nonNull(em)) {
                                needDecryption.add(Pair.create(message, em));
                                sessions.add(Pair.create(em.getKeyLocationPolicy(), em.getSessionId()));
                            } else {
                                message.setCryptStatus(CryptStatus.DECRYPT_FAILED);
                            }
                        } catch (Exception e) {
                            message.setCryptStatus(CryptStatus.DECRYPT_FAILED);
                        }
                    }

                    if (needDecryption.isEmpty()) {
                        return Single.just(messages);
                    }

                    return getKeyPairs(accountId, sessions)
                            .map(keys -> {
                                for (Pair<Message, EncryptedMessage> pair : needDecryption) {
                                    Message message = pair.getFirst();
                                    EncryptedMessage em = pair.getSecond();

                                    try {
                                        AesKeyPair keyPair = keys.get(em.getSessionId());

                                        if (isNull(keyPair)) {
                                            message.setCryptStatus(CryptStatus.DECRYPT_FAILED);
                                            continue;
                                        }

                                        String key = message.isOut() ? keyPair.getMyAesKey() : keyPair.getHisAesKey();
                                        String decryptedBody = CryptHelper.decryptWithAes(em.getOriginalBody(), key);

                                        message.setDecryptedBody(decryptedBody);
                                        message.setCryptStatus(CryptStatus.DECRYPTED);
                                    } catch (Exception e) {
                                        message.setCryptStatus(CryptStatus.DECRYPT_FAILED);
                                    }
                                }

                                return messages;
                            });
                });
    }

    private Single<LongSparseArray<AesKeyPair>> getKeyPairs(final int accountId, final List<Pair<Integer, Long>> tokens) {
        return Single.create(emitter -> {
            LongSparseArray<AesKeyPair> keys = new LongSparseArray<>(tokens.size());

            for (Pair<Integer, Long> token : tokens) {
                if (emitter.isDisposed()) {
                    break;
                }

                final long sessionId = token.getSecond();
                final int keyPolicy = token.getFirst();

                AesKeyPair keyPair = store.keys(keyPolicy).findKeyPairFor(accountId, sessionId).blockingGet();

                if (nonNull(keyPair)) {
                    keys.append(sessionId, keyPair);
                }
            }

            emitter.onSuccess(keys);
        });
    }
}