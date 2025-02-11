package org.tkit.onecx.bookmark.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.Predicate;

import org.tkit.onecx.bookmark.domain.criteria.StaticBookmarkSearchCriteria;
import org.tkit.onecx.bookmark.domain.models.StaticBookmark;
import org.tkit.onecx.bookmark.domain.models.StaticBookmark_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;

@ApplicationScoped
public class StaticBookmarkDAO extends AbstractDAO<StaticBookmark> {

    public PageResult<StaticBookmark> findStaticBookmarksByCriteria(StaticBookmarkSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(StaticBookmark.class);
            var root = cq.from(StaticBookmark.class);

            List<Predicate> predicates = new ArrayList<>();
            addSearchStringPredicate(predicates, cb, root.get(StaticBookmark_.workspaceName), criteria.getWorkspaceName());
            cq.where(cb.or(cb.and(predicates.toArray(new Predicate[0]))));
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_GET_BY_STATIC_BOOKMARK_CRITERIA, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_GET_BY_STATIC_BOOKMARK_CRITERIA,

    }
}
