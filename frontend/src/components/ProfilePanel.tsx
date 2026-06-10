import React, { useEffect, useState } from 'react';
import { profileService } from '../services/api';
import { UserProfile } from '../types';
import './ProfilePanel.css';

interface ProfilePanelProps {
  isOpen: boolean;
  onClose: () => void;
}

// EN: Side panel for the logged-in user; shows favorite games and review history.
// RU: Боковая панель авторизованного пользователя; показывает избранные игры и историю отзывов.
const ProfilePanel: React.FC<ProfilePanelProps> = ({ isOpen, onClose }) => {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // EN: Profile data is loaded only when the panel opens to avoid unnecessary API calls.
  // RU: Данные профиля загружаются только при открытии панели, чтобы не делать лишние API-запросы.
  useEffect(() => {
    if (!isOpen) return;

    const loadProfile = async () => {
      try {
        setLoading(true);
        setError(null);
        setProfile(await profileService.getMyProfile());
      } catch (err: any) {
        setError(err.message || 'Failed to load profile');
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div className="profile-overlay" onClick={onClose}>
      <aside className="profile-panel" onClick={(event) => event.stopPropagation()}>
        <button className="profile-close" onClick={onClose} aria-label="Close profile">
          x
        </button>

        <header className="profile-header">
          <span className="profile-kicker">User Profile</span>
          <h2>{profile?.user.username || 'Profile'}</h2>
        </header>

        {loading && <p className="profile-state">Loading profile...</p>}
        {error && <p className="profile-state profile-error">{error}</p>}

        {profile && !loading && (
          <>
            <section className="profile-section">
              <h3>Favorite Games ({profile.favoriteGames.length})</h3>
              {profile.favoriteGames.length === 0 ? (
                <p className="profile-empty">No favorite games yet.</p>
              ) : (
                <div className="profile-list">
                  {profile.favoriteGames.map(product => (
                    <article key={product.id} className="profile-row">
                      {product.imageUrl && <img src={product.imageUrl} alt={product.title} />}
                      <div>
                        <strong>{product.title}</strong>
                        <span>{product.description || 'No description'}</span>
                      </div>
                    </article>
                  ))}
                </div>
              )}
            </section>

            <section className="profile-section">
              <h3>Reviews ({profile.comments.length})</h3>
              {profile.comments.length === 0 ? (
                <p className="profile-empty">No comments yet.</p>
              ) : (
                <div className="profile-list">
                  {profile.comments.map(comment => (
                    <article key={comment.commentId} className="profile-comment">
                      <strong>{comment.productTitle}</strong>
                      <p>{comment.description}</p>
                      <span>{new Date(comment.creationDate).toLocaleDateString()}</span>
                    </article>
                  ))}
                </div>
              )}
            </section>
          </>
        )}
      </aside>
    </div>
  );
};

export default ProfilePanel;
