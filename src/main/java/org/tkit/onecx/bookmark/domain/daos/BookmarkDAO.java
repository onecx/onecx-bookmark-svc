package org.tkit.onecx.bookmark.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import org.tkit.onecx.bookmark.domain.criteria.BookmarkSearchCriteria;
import org.tkit.onecx.bookmark.domain.models.Bookmark;
import org.tkit.onecx.bookmark.domain.models.Bookmark_;
import org.tkit.onecx.bookmark.domain.models.enums.Scope;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class BookmarkDAO extends AbstractDAO<Bookmark> {

    public PageResult<Bookmark> findBookmarksByCriteria(BookmarkSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Bookmark.class);
            var root = cq.from(Bookmark.class);

            List<Predicate> predicates = new ArrayList<>();
            addSearchStringPredicate(predicates, cb, root.get(Bookmark_.workspaceName), criteria.getWorkspaceName());
            addSearchStringPredicate(predicates, cb, root.get(Bookmark_.productName), criteria.getProductName());
            addSearchStringPredicate(predicates, cb, root.get(Bookmark_.appId), criteria.getAppId());

            if (criteria.getScope() == null) {
                predicates.add(cb.or(
                        cb.equal(root.get(Bookmark_.SCOPE), Scope.PUBLIC.name()),
                        cb.and(
                                cb.equal(root.get(Bookmark_.SCOPE), Scope.PRIVATE.name()),
                                cb.equal(root.get(Bookmark_.userId), ApplicationContext.get().getPrincipal()))));
            } else if (criteria.getScope().equals(Scope.PUBLIC)) {
                predicates.add(cb.equal(root.get(Bookmark_.SCOPE), Scope.PUBLIC.name()));
            } else {
                predicates.add(cb.and(
                        cb.equal(root.get(Bookmark_.SCOPE), Scope.PRIVATE.name()),
                        cb.equal(root.get(Bookmark_.userId), ApplicationContext.get().getPrincipal())));
            }

            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_GET_BY_BOOKMARK_CRITERIA, ex);
        }
    }

    public Stream<Bookmark> findAllBookmarksByWorkspaceAndScope(String workspaceName, List<Scope> scopes) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Bookmark.class);
            var root = cq.from(Bookmark.class);

            List<Predicate> predicates = new ArrayList<>();
            addSearchStringPredicate(predicates, cb, root.get(Bookmark_.workspaceName), workspaceName);

            if (scopes.contains(Scope.PUBLIC) && scopes.contains(Scope.PRIVATE)) {
                predicates.add(cb.or(
                        cb.equal(root.get(Bookmark_.SCOPE), Scope.PUBLIC.name()),
                        cb.and(
                                cb.equal(root.get(Bookmark_.SCOPE), Scope.PRIVATE.name()),
                                cb.equal(root.get(Bookmark_.userId), ApplicationContext.get().getPrincipal()))));
            } else if (scopes.contains(Scope.PUBLIC)) {
                predicates.add(cb.equal(root.get(Bookmark_.SCOPE), Scope.PUBLIC.name()));
            } else {
                predicates.add(cb.and(
                        cb.equal(root.get(Bookmark_.SCOPE), Scope.PRIVATE.name()),
                        cb.equal(root.get(Bookmark_.userId), ApplicationContext.get().getPrincipal())));
            }

            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
            return this.getEntityManager().createQuery(cq).getResultStream();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_GET_ALL_BY_WORKSPACE_SCOPE, ex);
        }
    }

    public PageResult<Bookmark> findUserBookmarksByCriteria(BookmarkSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Bookmark.class);
            var root = cq.from(Bookmark.class);

            List<Predicate> predicates = new ArrayList<>();
            addSearchStringPredicate(predicates, cb, root.get(Bookmark_.workspaceName), criteria.getWorkspaceName());
            addSearchStringPredicate(predicates, cb, root.get(Bookmark_.productName), criteria.getProductName());
            addSearchStringPredicate(predicates, cb, root.get(Bookmark_.appId), criteria.getAppId());
            addSearchStringPredicate(predicates, cb, root.get(Bookmark_.userId), ApplicationContext.get().getPrincipal());
            addSearchStringPredicate(predicates, cb, root.get(Bookmark_.SCOPE), Scope.PRIVATE.name());

            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_GET_BY_BOOKMARK_CRITERIA, ex);
        }
    }

    @Transactional
    public void deleteAllByWorkspaceName(String workspace) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = this.deleteQuery();
            var root = cq.from(Bookmark.class);
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get(Bookmark_.workspaceName), workspace));
            predicates.add(cb.and(
                    cb.equal(root.get(Bookmark_.SCOPE), Scope.PRIVATE.name()),
                    cb.equal(root.get(Bookmark_.userId), ApplicationContext.get().getPrincipal())));
            cq.where(cb.or(cb.and(predicates.toArray(new Predicate[0]))));
            getEntityManager().createQuery(cq).executeUpdate();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_DELETE_ALL_BY_WORKSPACE, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_GET_BY_BOOKMARK_CRITERIA,
        ERROR_GET_ALL_BY_WORKSPACE_SCOPE,
        ERROR_DELETE_ALL_BY_WORKSPACE
    }
}
