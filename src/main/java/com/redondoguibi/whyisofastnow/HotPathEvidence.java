package com.redondoguibi.whyisofastnow;

import java.util.Objects;

/**
 * Records the measured reason and validation behind a hot-path optimization.
 *
 * <p>Keeping the profile reference beside the decision makes it harder for later changes to
 * treat an intuition or an unrelated benchmark as justification for enabling an optimization.</p>
 */
public record HotPathEvidence(
        String profileReference,
        String observation,
        String decision,
        String parityTest) {
    public HotPathEvidence {
        profileReference = requireText(profileReference, "profileReference");
        observation = requireText(observation, "observation");
        decision = requireText(decision, "decision");
        parityTest = requireText(parityTest, "parityTest");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank.");
        }
        return value;
    }
}
