package org.xubin.game.database.game.faction.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xubin.game.database.game.faction.entity.FactionMember;

public interface FactionMemberDao extends JpaRepository<FactionMember, Long> {
}
