import {motion} from 'framer-motion';
import {useState} from 'react';
import Swal from 'sweetalert2';
import Button from '../Button.tsx';
import {TeamService} from '../../service/TeamService.ts';
import {type TeamDTOSchema, type TeamFullSchema} from '../../heroAPI.ts';
import {useToast} from "../../hooks/useToast.ts";

interface CreateTeamDialogProps {
    onSuccess: () => void;
    editingTeam?: TeamFullSchema | null;
}

const validateForm = (formData: Partial<TeamDTOSchema>) => {
    const errors: Record<string, string> = {};

    if (!formData.name?.trim()) {
        errors.name = 'Name is required';
    }

    return errors;
};

const CreateTeamDialog = ({ onSuccess, editingTeam }: CreateTeamDialogProps) => {
    const [formData, setFormData] = useState<Partial<TeamDTOSchema>>(() => {
        if (editingTeam) {
            return {
                name: editingTeam.name,
                size: editingTeam.size,
            };
        }
        return {
            name: '',
            size: 0,
        };
    });
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const {showSuccess, showError} = useToast();

    const handleSubmit = async () => {
        const validationErrors = validateForm(formData);
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        setLoading(true);
        setErrors({});

        try {
            const submitData: TeamDTOSchema = {
                name: formData.name || '',
                size: Number(formData.size),
            };

            if (editingTeam) {
                await TeamService.updateTeam(editingTeam.id!, submitData);
                showSuccess('Team successfully updated!');
            } else {
                await TeamService.addTeam(submitData);
                showSuccess('Team successfully created!');
            }

            onSuccess();
            Swal.close();
        } catch (error: any) {
            let errorMessage = 'An error occurred while creating the team';

            if (error?.data?.message) {
                errorMessage = error.data.message;
            } else if (error?.message) {
                errorMessage = error.message;
            }

            showError(errorMessage);

            if (error?.data) {
                const serverErrors: Record<string, string> = {};

                if (typeof error.data === 'object') {
                    Object.keys(error.data).forEach(key => {
                        if (key !== 'message' && key !== 'code' && key !== 'time') {
                            serverErrors[key] = error.data[key];
                        }
                    });
                }

                if (Object.keys(serverErrors).length > 0) {
                    setErrors(serverErrors);
                }
            }
        } finally {
            setLoading(false);
        }
    };

    const updateField = (field: string, value: any) => {
        setFormData(prev => ({
            ...prev,
            [field]: value
        }));
        if (errors[field]) {
            setErrors(prev => ({...prev, [field]: ''}));
        }
    };

    const getInputStyle = (fieldName: string) => ({
        borderColor: errors[fieldName] ? 'var(--color-accent)' : 'black',
        boxShadow: errors[fieldName] ? '0 0 0 2px rgba(225, 61, 96, 0.1)' : 'none'
    });

    return (
        <div style={{padding: 'var(--spacing-lg)'}}>
            <div style={{display: 'flex', flexDirection: 'column', gap: 'var(--spacing-md)'}}>
                <div>
                    <label style={{
                        display: 'block',
                        marginBottom: 'var(--spacing-xs)',
                        fontFamily: 'var(--font-family-primary)'
                    }}>
                        Name *
                    </label>
                    <motion.input
                        type="text"
                        value={formData.name || ''}
                        onChange={(e) => updateField('name', e.target.value)}
                        style={{
                            padding: 'var(--spacing-sm)',
                            fontFamily: 'var(--font-family-primary)',
                            fontSize: 'var(--font-size-general)',
                            boxSizing: 'border-box',
                            border: 'var(--border-width) var(--border-style) black',
                            borderRadius: 'var(--border-radius)',
                            ...getInputStyle('name')
                        }}
                        whileFocus={{
                            scale: 1.02,
                            borderColor: errors.name ? 'var(--color-accent)' : 'var(--color-primary)',
                            boxShadow: errors.name ? '0 0 0 2px rgba(225, 61, 96, 0.1)' : '0 0 0 2px rgba(59, 130, 246, 0.1)'
                        }}
                    />
                    {errors.name && (
                        <motion.span
                            initial={{opacity: 0, y: -10}}
                            animate={{opacity: 1, y: 0}}
                            style={{color: 'var(--color-accent)', fontSize: 'var(--font-size-sm)'}}
                        >
                            {errors.name}
                        </motion.span>
                    )}
                </div>
            </div>

            <div style={{
                display: 'flex',
                justifyContent: 'center',
                gap: 'var(--spacing-sm)',
                marginTop: 'var(--spacing-lg)'
            }}>
                <Button variant="gray" onClick={() => Swal.close()} disabled={loading}>
                    Cancel
                </Button>
                <Button
                    variant="success"
                    onClick={handleSubmit}
                    disabled={loading}
                >
                    {loading ? (editingTeam ? 'Updating...' : 'Creating...') : (editingTeam ? 'Update' : 'Create')}
                </Button>
            </div>
        </div>
    );
};

export default CreateTeamDialog;